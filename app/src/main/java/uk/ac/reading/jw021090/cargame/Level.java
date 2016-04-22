package uk.ac.reading.jw021090.cargame;

/**
 * Created by Kikadass on 21/04/2016.
 */
public class Level{

    private int maxSpeed;
    private int maxCars;
    private int minLanes;
    private boolean shooting;

    public Level(int maxSpeed, int maxCars, int minLanes, boolean shooting){
        this.maxSpeed = maxSpeed;
        this.maxCars = maxCars;
        this.minLanes = minLanes;
        this.shooting = shooting;
    }

    public boolean isShooting() {
        return shooting;
    }

    public int getMinLanes() {
        return minLanes;
    }

    public int getMaxCars() {
        return maxCars;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMaxCars(int maxCars) {
        this.maxCars = maxCars;
    }

    public void setMinLanes(int minLanes) {
        this.minLanes = minLanes;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

}
