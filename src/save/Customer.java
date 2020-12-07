package save;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class Customer {
    private String name;
    private String city;
    private Date birthDate;

    public Customer setName(String value) {
        if (value != null && value.length() > 0) {
            this.name = value;
            return this;
        }
        throw new IllegalArgumentException("Name shouldn't be empty");
    }

    public Customer setBirthDate(String value) {
        try {
            DateFormat FORMATTER = new SimpleDateFormat("dd-MMM-yyyy");
            birthDate = FORMATTER.parse(value);
            return this;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Date of Birth shouldn't be empty");
        } catch (Throwable e) {
            throw new IllegalArgumentException(String.format("Incorrect Date of Birth (%s)", e.getMessage()));
        }
    }

    public Customer setCity(Object value) {
        String city;
        if (value != null && (city = (String) value).length() > 0) {
            this.city = city;
            return this;
        }
        throw new IllegalArgumentException("Please, select your current degree");
    }

    public static final String NAME = "name";

    public String getName() {
        return name;
    }

    public static final String BIRTH_DATE = "birth";

    public String getBirth() {
        LocalDate date = LocalDate.ofInstant(birthDate.toInstant(), ZoneId.systemDefault());
        /**
         * Or, for JDK < 9
         * java.time.ZonedDateTime zdt = birthDate.toInstant().atZone(java.time.ZoneId.systemDefault());
         * LocalDate date = zdt.toLocalDate();
         */
        return String.format("%d, %d %s", date.getYear(), date.getDayOfMonth(), date.getMonth().getDisplayName(TextStyle.FULL, Locale.UK));
    }

    public static final String CITY = "city";

    public String getCity() {
        return city;
    }

    public Date getBirthDate() {
        return (Date) birthDate.clone();
    }
}
