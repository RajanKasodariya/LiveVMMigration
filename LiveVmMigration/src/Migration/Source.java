package Migration;

import static Migration.Instructions.*;

import java.io.IOException;

class RunCPU extends Thread{
	VM vm;
	
	public RunCPU(VM vm){
		this.vm=vm;
	}
	
	public void run(){
		try {
			vm.cpu();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
};

class StartPreCopyMigration extends Thread{
	VM vm;
	public StartPreCopyMigration(VM vm){
		this.vm=vm;
	}
	public void run(){
		vm.sendVM();            // sends VM copy
		vm.migrate();			// migrates RAM pages iteratively
				// migrates CPU states
	}
}

public class Source {
	
	@SuppressWarnings("deprecation")
	public static void main(String args[]){
				
		/* Time measure */
		long startTime_T;
		long startTime_D;
		long endTime_T;
		long endTime_D;
		long TotalMigrationTime;
		long DownTime;
		
		
		VM vm=new VM(Program.code6,1000);
		
		System.out.println("Len : "+Program.code2.length);
		
		RunCPU runcpu=new RunCPU(vm);
		Thread t1=new Thread(runcpu);
		
		/* Runs CPU on Thread 1*/
		t1.start();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		startTime_T=System.currentTimeMillis();
		/* Initiate Migration */
		Thread t2=new Thread(new StartPreCopyMigration(vm));
		t2.start();
		
		try {
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t1.stop();
		
		startTime_D=System.currentTimeMillis();
		vm.migrateStates();
		
		endTime_T=System.currentTimeMillis();
		
		TotalMigrationTime = endTime_T-startTime_T;
		DownTime = endTime_T-startTime_D;
		
		System.out.println("Total Migration Time : \t"+TotalMigrationTime+
							"\nDownTime : \t"+DownTime);
		
		System.out.println("Total Pages Migrated : "+vm.totalPagesMigrated);

		System.out.println("Migration completed...");
	}
}
