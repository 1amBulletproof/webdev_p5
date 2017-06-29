/** CONTROLLER class
 *
 * @author Brandon Tarney
 * @since 6/15/2017
 */
package com.brandontarney.controller;

import com.brandontarney.bookingrate.BookingDay;
import com.brandontarney.bookingrate.Rates;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller (MVC) - MVC Architecture: "Gui" = View, "Controller" = Controller,
 * "Rates" = Model
 */
public class Controller {

    /**
     * Compute the rate associated with a hike
     *
     * @param rate preliminary rate information of hike
     * @param duration duration of hike
     * @param year time of hike
     * @param month time of hike
     * @param day time of hike
     *
     * @return Final rate information for hike
     * @throws BadRateException, IOException
     */
    public static String[] computeRate(
            Rates.HIKE hike, int duration, int year, int month, int day)
            throws BadRateException, IOException {

        //1. Create a socket object
        String host = "128.220.101.240";
        int port = 20025;
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(host, port);

            //2. Create an output stream to send data to the socket
            out = new PrintWriter(socket.getOutputStream(), true);
            //3. Create an input stream to read server response from the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: web6.jhuep.com");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for web6.jhuep.com");
            System.exit(1);
        }

        //4. Do I/O with the server using I/O streams
        int hikeInt = hike.getValue();
        String serverInput = "" + hikeInt + ":" + year + ":" + month + ":" + day + ":" + duration;
        out.println(serverInput);

        String serverInfo = in.readLine();

        //5. Close stuff
        out.close();
        in.close();
        socket.close();

        //returnRateAndDescription[0] = rate, returnRateAndDescription[1] = description
        String[] returnRateAndDescription = parseServerString(serverInfo);

        if (returnRateAndDescription[0] == null
                || returnRateAndDescription[0].isEmpty()) {
            throw new BadRateException("No Rate Provided by Server");
        } else if (returnRateAndDescription[0].equals("-0.01")) {
            throw new BadRateException(returnRateAndDescription[1]);
        }

        returnRateAndDescription[1] = getHikeDetails(
                hike, duration, year, month, day);

        return returnRateAndDescription;
    }

    public static String[] parseServerString(String serverInfo) {
        String[] returnVal;
        returnVal = serverInfo.split(":");
        return returnVal;
    }

    public static String getHikeDetails(
            Rates.HIKE hike, int duration, int year, int month, int day) {
        StringBuilder summary = new StringBuilder();

        BookingDay beginDate = new BookingDay(year, month, day);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = beginDate.getDate().getTime();
        String startDateStr = df.format(startDate);

        summary = summary.append("You just booked a hike!");
        summary = summary.append("\nHike: " + hike.name());
        summary = summary.append("\nStart: " + startDateStr);
        summary = summary.append("\nLength:  " + duration);

        return summary.toString();
    }

    /**
     * (OLD, NOT valid for network server modification) 
     * Test the basic logic
     * behind booking classes
     */
    public static void main(String[] args) {
        System.out.println("LogicTest Start");

        BookingDay birthday = new BookingDay(2017, 11, 5);
        System.out.println("Booking Date = " + birthday.toString());

        BookingDay endOfTour = new BookingDay(2017, 11, 8);

        Rates gardinerRate = new Rates(Rates.HIKE.GARDINER);
        Rates hellroaringRate = new Rates(Rates.HIKE.HELLROARING);
        Rates beatenRate = new Rates(Rates.HIKE.BEATEN);

        gardinerRate.setSeasonStart(1, 1);
        gardinerRate.setSeasonEnd(12, 30);
        gardinerRate.setBeginDate(birthday);
        gardinerRate.setEndDate(endOfTour);

        System.out.println("Birthday valid? " + birthday.isValidDate());
        System.out.println("Birhday before endOfTour? " + birthday.before(endOfTour));
        int[] possibleDurations = gardinerRate.getDurations();
        for (int i = 0; i < possibleDurations.length; i++) {
            System.out.println("Can hike for " + possibleDurations[i] + " days");
        }

        System.out.println("Base Weekday Rate: $" + gardinerRate.getBaseRate());
        System.out.println("Base Weekend Rate: $" + gardinerRate.getPremiumRate());
        System.out.println("Valid trip dates? " + gardinerRate.isValidDates());
        System.out.println("Trip is " + gardinerRate.getNormalDays() + " weekdays and "
                + gardinerRate.getPremiumDays() + " weekend-days long");

        System.out.println("Cost of tour is $" + gardinerRate.getCost());

    }

}
