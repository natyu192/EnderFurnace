package com.gmail.mckokumin.enderfurnace.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TickUpdateEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private int tick;

	public TickUpdateEvent(int tick){
		this.tick = tick;
	}

	public int getTick() {
		return tick;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
