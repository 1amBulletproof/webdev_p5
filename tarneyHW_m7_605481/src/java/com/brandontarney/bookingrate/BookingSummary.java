/**
 * BookingSummary Class
 * - Summarize a booking
 *
 * @author Brandon Tarney
 * @since 6/17/2017
 */
package com.brandontarney.bookingrate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingSummary {

    public static String summarize(Rates rate) {
        StringBuilder summary = new StringBuilder();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = rate.getBeginDate().getTime();
        Date endDate = rate.getEndDate().getTime();
        String startDateStr = df.format(startDate);
        String endDateStr = df.format(endDate);
        
        summary = summary.append("You just booked a hike!");
        summary = summary.append("\nHike: " + rate.getHike().name() ); 
        summary = summary.append("\nStart: " + startDateStr);
        summary = summary.append("\nEnd:  " + endDateStr);
        summary = summary.append("\nCost: $" + Double.toString(rate.getCost()));
        return summary.toString();

    }

}
