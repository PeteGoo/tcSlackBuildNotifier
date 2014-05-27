package slacknotifications.teamcity.payload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import slacknotifications.teamcity.Loggers;

import jetbrains.buildServer.serverSide.SBuildServer;

public class SlackNotificationPayloadManager {
	
	HashMap<String, SlackNotificationPayload> formats = new HashMap<String,SlackNotificationPayload>();
	Comparator<SlackNotificationPayload> rankComparator = new SlackNotificationPayloadRankingComparator();
	List<SlackNotificationPayload> orderedFormatCollection = new ArrayList<SlackNotificationPayload>();
	SBuildServer server;
	
	public SlackNotificationPayloadManager(SBuildServer server){
		this.server = server;
		Loggers.SERVER.info("SlackNotificationPayloadManager :: Starting");
	}
	
	public void registerPayloadFormat(SlackNotificationPayload payloadFormat){
		Loggers.SERVER.info(this.getClass().getSimpleName() + " :: Registering payload " 
				+ payloadFormat.getFormatShortName() 
				+ " with rank of " + payloadFormat.getRank());
		formats.put(payloadFormat.getFormatShortName(),payloadFormat);
		this.orderedFormatCollection.add(payloadFormat);
		
		Collections.sort(this.orderedFormatCollection, rankComparator);
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Payloads list is " + this.orderedFormatCollection.size() + " items long. Payloads are ranked in the following order..");
		for (SlackNotificationPayload pl : this.orderedFormatCollection){
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Payload Name: " + pl.getFormatShortName() + " Rank: " + pl.getRank());
		}
	}

	public SlackNotificationPayload getFormat(String formatShortname){
		if (formats.containsKey(formatShortname)){
			return formats.get(formatShortname);
		}
		return null;
	}
	
	public Boolean isRegisteredFormat(String format){
		return formats.containsKey(format);
	}
	
	public Set<String> getRegisteredFormats(){
		return formats.keySet();
	}
	
	public Collection<SlackNotificationPayload> getRegisteredFormatsAsCollection(){
		return orderedFormatCollection;
	}

	public SBuildServer getServer() {
		return server;
	}	
	
	
}