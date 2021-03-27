package com.bencodez.advancedcore.api.time;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;

import com.bencodez.advancedcore.AdvancedCorePlugin;
import com.bencodez.advancedcore.api.time.events.DateChangedEvent;
import com.bencodez.advancedcore.api.time.events.DayChangeEvent;
import com.bencodez.advancedcore.api.time.events.MonthChangeEvent;
import com.bencodez.advancedcore.api.time.events.PreDateChangedEvent;
import com.bencodez.advancedcore.api.time.events.WeekChangeEvent;

/**
 * The Class TimeChecker.
 */
public class TimeChecker {

	private Timer timer = new Timer();

	private AdvancedCorePlugin plugin;

	private boolean timerLoaded = false;

	private boolean processing = false;

	public TimeChecker(AdvancedCorePlugin plugin) {
		this.plugin = plugin;
	}

	public void forceChanged(TimeType time) {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				forceChanged(time, true, true, true);
			}
		});
	}

	private void forceChanged(TimeType time, boolean fake, boolean preDate, boolean postDate) {
		processing = true;
		try {
			plugin.debug("Executing time change events: " + time.toString());
			plugin.getLogger().info("Time change event: " + time.toString() + ", Fake: " + fake);
			if (preDate) {
				PreDateChangedEvent preDateChanged = new PreDateChangedEvent(time);
				preDateChanged.setFake(fake);
				plugin.getServer().getPluginManager().callEvent(preDateChanged);
			}
			if (time.equals(TimeType.DAY)) {
				DayChangeEvent dayChange = new DayChangeEvent();
				dayChange.setFake(fake);
				plugin.getServer().getPluginManager().callEvent(dayChange);
			} else if (time.equals(TimeType.WEEK)) {
				WeekChangeEvent weekChange = new WeekChangeEvent();
				weekChange.setFake(fake);
				plugin.getServer().getPluginManager().callEvent(weekChange);
			} else if (time.equals(TimeType.MONTH)) {
				MonthChangeEvent monthChange = new MonthChangeEvent();
				monthChange.setFake(fake);
				plugin.getServer().getPluginManager().callEvent(monthChange);
			}

			if (postDate) {
				DateChangedEvent dateChanged = new DateChangedEvent(time);
				dateChanged.setFake(fake);
				plugin.getServer().getPluginManager().callEvent(dateChanged);
			}

			plugin.debug("Finished executing time change events: " + time.toString());
			processing = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public LocalDateTime getTime() {
		return LocalDateTime.now().plusHours(AdvancedCorePlugin.getInstance().getOptions().getTimeHourOffSet());
	}

	public boolean hasDayChanged(boolean set) {
		int prevDay = plugin.getServerDataFile().getPrevDay();
		int day = getTime().getDayOfMonth();

		if (prevDay == day) {
			return false;
		}
		if (set) {
			plugin.getServerDataFile().setPrevDay(day);
		}
		return true;
	}

	public boolean hasMonthChanged(boolean set) {
		String prevMonth = plugin.getServerDataFile().getPrevMonth();
		String month = getTime().getMonth().toString();
		if (prevMonth.equals(month)) {
			return false;
		}
		if (set) {
			plugin.getServerDataFile().setPrevMonth(month);
		}
		return true;

	}

	public boolean hasTimeOffSet() {
		return AdvancedCorePlugin.getInstance().getOptions().getTimeHourOffSet() != 0;
	}

	public boolean hasWeekChanged(boolean set) {
		int prevDate = plugin.getServerDataFile().getPrevWeekDay();
		LocalDateTime date = getTime();
		TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		int weekNumber = date.get(woy);
		if (weekNumber == prevDate) {
			return false;
		}
		if (set) {
			plugin.getServerDataFile().setPrevWeekDay(weekNumber);
		}
		return true;
	}

	public void loadTimer(int minutes) {
		if (!timerLoaded) {
			timerLoaded = true;
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (plugin != null) {
						if (!processing) {
							update();
						}
					} else {
						cancel();
						timerLoaded = false;
					}

				}
			}, 60 * 1000, minutes * 60 * 1000);
		} else {
			AdvancedCorePlugin.getInstance().debug("Timer is already loaded");
		}
	}

	/**
	 * Update.
	 */
	public void update() {
		if (plugin == null) {
			return;
		}
		if (hasTimeOffSet()) {
			plugin.extraDebug("TimeHourOffSet: " + getTime().getHour() + ":" + getTime().getMinute());
		}

		if (plugin.getServerDataFile().isIgnoreTime()) {
			hasDayChanged(true);
			hasMonthChanged(true);
			hasWeekChanged(true);
			plugin.getServerDataFile().setIgnoreTime(false);
			plugin.getLogger().info("Ignoring time change events for one time only");
		}

		if (!processing) {
			// stagger process time change events to prevent overloading mysql table
			if (hasMonthChanged(false)) {
				plugin.getLogger().info("Detected month changed, processing...");
				forceChanged(TimeType.MONTH, false, true, true);
				hasMonthChanged(true);
			} else if (hasWeekChanged(false)) {
				plugin.getLogger().info("Detected week changed, processing...");
				forceChanged(TimeType.WEEK, false, true, true);
				hasWeekChanged(true);
			} else if (hasDayChanged(false)) {
				plugin.getLogger().info("Detected day changed, processing...");
				forceChanged(TimeType.DAY, false, true, true);
				hasDayChanged(true);
			}
		}
	}
}