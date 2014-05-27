package slacknotifications.teamcity.payload;

import java.util.Comparator;

public class SlackNotificationPayloadRankingComparator implements Comparator<SlackNotificationPayload> {
	
	public int compare(SlackNotificationPayload payload1, SlackNotificationPayload payload2) {
		if (payload1.getRank() > payload2.getRank()){
			return -1;
		} else if (payload1.getRank() < payload2.getRank()){
			return 1;
		} else {
			return 0;
		}
	}
}