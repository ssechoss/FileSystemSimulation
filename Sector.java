
package fileSystemSimulation;

import static fileSystemSimulation.Command.SECTOR_SIZE;

public class Sector {

    private Sector forward;
    private Sector back;
    private boolean isFree ;
   
    public Sector() {
        this.isFree=true;
        this.back=back;
        this.forward=forward;        
    }
    public void initialize(){
       isFree=true;
    }
    public Sector getForward() {
        return forward;
    }

    public void setForward(Sector forward) {
        this.forward = forward;
    }

    public Sector getBack() {
        return back;
    }

    public void setBack(Sector back) {
        this.back = back;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }
}
