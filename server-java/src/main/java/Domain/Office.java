package Domain;

import java.util.Objects;

public class Office extends Entity<Long> {
    private String address;

    public Office(Long id, String address) {
        super(id);
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Office office = (Office) o;
        return Objects.equals(address, office.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}