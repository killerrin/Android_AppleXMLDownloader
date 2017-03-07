package com.killerrin.andre.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by andre on 2017-03-06.
 */

public class ParseApplications {
    private String xmlData;
    private ArrayList<Application> applications;

    public ParseApplications(String xmlData) {
        this.xmlData = xmlData;
        applications = new ArrayList<Application>();
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    public boolean process() {
        boolean status = true;
        Application currentRecord = new Application();
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(this.xmlData));
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        //Log.d("ParseApplications", "Starting tag for " + tagName);
                        if (tagName.equalsIgnoreCase("entry")) {
                            inEntry = true;
                            currentRecord = new Application();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        //Log.d("ParseApplications", "Ending tag for " + tagName);
                        if (inEntry) {
                             if (tagName.equalsIgnoreCase("entry")) {
                                 applications.add(currentRecord);
                                 inEntry = false;
                             } else if (tagName.equalsIgnoreCase("name")) {
                                 currentRecord.setName(textValue);
                             } else if (tagName.equalsIgnoreCase("artist")) {
                                 currentRecord.setArtist(textValue);
                             } else if (tagName.equalsIgnoreCase("releaseDate")) {
                                 currentRecord.setReleaseData(textValue);
                             }
                        }
                        break;
                    default:
                        // Nothing else to do.
                }
                eventType = xpp.next();
            }
        }
        catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        for (Application app: applications) {
            Log.d("ParseApplications", "*******************************");
            Log.d("ParseApplications", "Name: " + app.getName());
            Log.d("ParseApplications", "Artist: " + app.getArtist());
            Log.d("ParseApplications", "Release Date: " + app.getReleaseData());
            Log.d("ParseApplications", "*******************************");
        }

        return true;
    }
}
