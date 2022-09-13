package net.set.spawn.mod;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Seed {
    private String seed;
    private String seedName;
    private double x;
    private double z;

    public Seed(){
    }

    public Seed(String seed, String seedName, double x, double z) {
        this.seed = seed;
        this.seedName = seedName;
        this.x = x;
        this.z = z;
    }

    public String getSeed() {
        return seed;
    }

    public String getSeedName() {
        return seedName;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }
}
