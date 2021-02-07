package Models;

import java.io.Serializable;
import java.util.Date;

public class Customers implements Serializable {
    private String Name;
    private String Location;
    private Date BirthDate;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Date getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(Date birthDate) {
        BirthDate = birthDate;
    }

    public Customers(String Name, java.sql.Date BirthDate, String Location) {
        this.Name = Name;
        this.BirthDate = BirthDate;
        this.Location = Location;
    }

}
