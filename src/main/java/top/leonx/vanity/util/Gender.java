package top.leonx.vanity.util;

public enum Gender {
    MALE, FEMALE,BOTH;

    public boolean isSuitable(Gender gender)
    {
        if(this==BOTH)
            return true;

        return this.equals(gender);
    }
}
