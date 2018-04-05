import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

public class TemperatureSensorServer extends UnicastRemoteObject implements
		TemperatureSensor, Runnable {
                
	private volatile double temp; //variable declare.
	private ArrayList<TemperatureListener> list = new ArrayList<TemperatureListener>();

        // TemperatureSensorServer Constructor.
	public TemperatureSensorServer() throws java.rmi.RemoteException {
		
                temp = 98.0;
	}
        
        // this methord return current temperature.
	public double getTemperature() throws java.rmi.RemoteException {
		
                return temp;
	}

        // add new client.
	public void addTemperatureListener(TemperatureListener listener)throws java.rmi.RemoteException {
		
                System.out.println("adding listener -" + listener);
		list.add(listener);
	}

        // Removing a client
	public void removeTemperatureListener(TemperatureListener listener)throws java.rmi.RemoteException {
		
                System.out.println("removing listener -" + listener);
		list.remove(listener);
	
        }
        
        // impliment run methord.
	public void run() {
		
                Random r = new Random();
		for (;;) {
			try {
				// Sleep for a random amount of time
				int duration = r.nextInt() % 10000 + 200;
                                
				// Check to see if negative, if so, reverse
				if (duration < 0) {
					duration = duration * -1;
					Thread.sleep(duration);
				}
			} catch (InterruptedException ie) {
			}

			// Get a number, to see if temp goes up or down
			int num = r.nextInt();
			if (num < 0) {
				temp += 0.5;
			} else {
				temp -= 0.5;
			}

			// Notify registered listeners
			notifyListeners();
		}
	}

	private void notifyListeners() {
            
            //Notify every listener in the registered list
            
            Iterator<TemperatureListener> ite = list.iterator();
            while(ite.hasNext()){
                TemperatureListener listener = ite.next();
                //Notify, if possible a listener
                try{
                    listener.temperatureChanged(temp);
                } catch(RemoteException re){
                    re.printStackTrace();
                }
            }
            
	}

	public static void main(String[] args) {
		
                System.out.println("Loading temperature service");

		try {
			TemperatureSensorServer sensor = new TemperatureSensorServer();
			String registry = "localhost";

			String registration = "rmi://" + registry + "/TemperatureSensor";

			Naming.rebind(registration, sensor);

			Thread thread = new Thread(sensor);
			thread.start();
		} catch (RemoteException re) {
			System.err.println("Remote Error - " + re);
		} catch (Exception e) {
			System.err.println("Error - " + e);
		}

	}

}