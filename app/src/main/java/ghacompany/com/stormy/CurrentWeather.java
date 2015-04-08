package ghacompany.com.stormy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ayoub on 4/6/2015.
 */
public class CurrentWeather {

    private String mIcon;
    private long mTime;
    private int mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;

    public String getIcon() {

        return mIcon;
    }

    public void setIcon(String icon) {

        mIcon = icon;
    }


    public int getIconId() {

        int iconId = R.mipmap.clear_day;
        if (mIcon.equals("clear-day")) {
            iconId = R.mipmap.clear_day;
        }
        else if (mIcon.equals("clear-night")) {
            iconId = R.mipmap.clear_night;
        }
        else if (mIcon.equals("rain")) {
            iconId = R.mipmap.rain;
        }
        else if (mIcon.equals("snow")) {
            iconId = R.mipmap.snow;
        }
        else if (mIcon.equals("sleet")) {
            iconId = R.mipmap.sleet;
        }
        else if (mIcon.equals("wind")) {
            iconId = R.mipmap.wind;
        }
        else if (mIcon.equals("fog")) {
            iconId = R.mipmap.fog;
        }
        else if (mIcon.equals("cloudy")) {
            iconId = R.mipmap.cloudy;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.mipmap.partly_cloudy;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.mipmap.cloudy_night;
        }
        return iconId;
    }

    public long getTime() {

        return mTime;
    }

    public String getTimeZone() {

        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getFormattedTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime() * 1000);
        String timeString = formatter.format(dateTime);

        return  timeString;
    }

    public void setTime(long time) {

        mTime = time;
    }

    public int getTemperature() {

        return Math.round(mTemperature);
    }

    public void setTemperature(int temperature) {

        mTemperature = temperature;
    }

    public double getHumidity() {

        return mHumidity;
    }

    public void setHumidity(double humidity) {

        mHumidity = humidity;
    }

    public double getPrecipChance() {

        return mPrecipChance;
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {

        return mSummary;
    }

    public void setSummary(String summary) {

        mSummary = summary;
    }

}
