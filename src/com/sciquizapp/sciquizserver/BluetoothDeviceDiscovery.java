package com.sciquizapp.sciquizserver;
import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
/**
 * Class that discovers all bluetooth devices in the neighbourhood
 * and displays their name and bluetooth address.
 */
public class BluetoothDeviceDiscovery implements DiscoveryListener{
    //object used for waiting

    public static Object lock=new Object();
    //vector containing the devices discovered
    public static Vector vecDevices=new Vector();
    public String clientUrl = "btspp://E89309B31623:5;authenticate=false;encrypt=false;master=false";
    //methods of DiscoveryListener
    /**
     * This call back method will be called for each discovered bluetooth devices.
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        //System.out.println("Device discovered: "+btDevice.getBluetoothAddress());
        //add the device to the vector
        if(!vecDevices.contains(btDevice)){
            vecDevices.addElement(btDevice);
            //  sterm.joro.append(btDevice.getBluetoothAddress(), null);

        }
    }
    //no need to implement this method since services are not being discovered
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        if(servRecord!=null && servRecord.length>0){
            clientUrl = servRecord[0].getConnectionURL(0,false);
        }
        synchronized(lock){
            lock.notify();
        }
    }
    //no need to implement this method since services are not being discovered
    public void serviceSearchCompleted(int transID, int respCode) {
    }
    /**
     * This callback method will be called when the device discovery is
     * completed.
     */

    public void inquiryCompleted(int discType) {
        synchronized(lock){
            lock.notify();
        }
        //switch (discType) {
        // case DiscoveryListener.INQUIRY_COMPLETED :
        //  System.out.println("INQUIRY_COMPLETED");

        //  break;
        // case DiscoveryListener.INQUIRY_TERMINATED :
        //  System.out.println("INQUIRY_TERMINATED");
        //  break;
        // case DiscoveryListener.INQUIRY_ERROR :
        //  System.out.println("INQUIRY_ERROR");
        //  break;
        // default :
        //  System.out.println("Unknown Response Code");
        //  break;
        //}
    }//end method
}//end class
