package com.bencodez.advancedcore.api.user.usercache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bencodez.advancedcore.api.misc.ArrayUtils;
import com.bencodez.advancedcore.api.user.AdvancedCoreUser;
import com.bencodez.advancedcore.api.user.usercache.change.UserDataChange;
import com.bencodez.advancedcore.api.user.usercache.keys.UserDataKey;
import com.bencodez.advancedcore.api.user.usercache.value.DataValue;

import lombok.Getter;

public class UserDataCache {
	@Getter
	private HashMap<String, DataValue> cache;

	private Queue<UserDataChange> cachedChanges;

	private UserDataManager manager;
	private boolean scheduled = false;
	@Getter
	private UUID uuid;

	public UserDataCache(UserDataManager manager, UUID uuid) {
		this.uuid = uuid;
		this.manager = manager;
		cachedChanges = new ConcurrentLinkedQueue<UserDataChange>();
		cache = new HashMap<String, DataValue>();
	}

	public synchronized void addChange(UserDataChange change) {
		cache.put(change.getKey(), change.toUserDataValue());
		cachedChanges.add(change);
		if (!scheduled) {
			scheduleChanges();
		}
	}

	public UserDataCache cache() {
		if (uuid != null) {
			AdvancedCoreUser user = getUser();
			ArrayList<String> keys = user.getUserData().getKeys();
			HashMap<String, DataValue> data = user.getUserData().getValues();
			for (UserDataKey dataKey : manager.getKeys()) {
				String key = dataKey.getKey();
				keys.remove(key);
				if (data.containsKey(key)) {
					DataValue dataValue = data.get(key);
					manager.getPlugin().devDebug("Caching " + dataValue.getTypeName() + " " + key + " for "
							+ uuid.toString() + ", value: " + dataValue.toString());
					cache.put(key, dataValue);
				}

			}
			if (keys.size() > 0) {
				manager.getPlugin().devDebug("Keys not cached: " + ArrayUtils.getInstance().makeStringList(keys));
			}
		}
		return this;
	}

	public void clearCache() {
		if (hasChangesToProcess()) {
			processChanges();
		}
		cache.clear();
	}

	public void dump() {
		if (hasChangesToProcess()) {
			processChanges();
		}
		cache = null;
		cachedChanges = null;
		uuid = null;
	}

	public AdvancedCoreUser getUser() {
		return manager.getPlugin().getUserManager().getUser(uuid, false);
	}

	public boolean hasCache() {
		return !cache.isEmpty();
	}

	public boolean hasChangesToProcess() {
		return !cachedChanges.isEmpty();
	}

	public boolean isCached(String key) {
		return cache.containsKey(key);
	}

	public void processChanges() {
		if (uuid != null) {
			if (cachedChanges.size() > 0) {
				manager.getPlugin()
						.extraDebug("Processing changes for " + uuid.toString() + ", Changes: " + cachedChanges.size());
				AdvancedCoreUser user = getUser();
				HashMap<String, DataValue> values = new HashMap<String, DataValue>();
				while (!cachedChanges.isEmpty()) {
					UserDataChange change = cachedChanges.poll();
					values.put(change.getKey(), change.toUserDataValue());
					change.dump();
					// manager.getPlugin().extraDebug("Processing change for " + change.getKey());
				}
				if (!values.isEmpty()) {
					user.getUserData().setValues(values);
				}
			}
		}
	}

	private void scheduleChanges() {
		manager.getPlugin().debug("Schedule changes");
		scheduled = true;
		manager.getTimer().schedule(new TimerTask() {

			@Override
			public void run() {
				processChanges();
				scheduled = false;
			}
		}, 1000 * 3);
	}

	public void updateCache(HashMap<String, DataValue> tempCache) {
		cache = tempCache;
	}
}
