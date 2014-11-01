package sos.base.util.sosLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import sos.base.SOSConstant.ModeType;
import sos.base.SOSConstant.SystemType;
import sos.base.SOSWorldModel;
import sos.base.entities.AmbulanceCenter;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.FireBrigade;
import sos.base.entities.FireStation;
import sos.base.entities.PoliceForce;
import sos.base.entities.PoliceOffice;
import sos.base.entities.StandardEntity;
import sos.base.util.sosLogger.SOSLoggerSystem.OutputType;


public class OldLogger {
	private ModeType modeType;    /* Light/Medium/Heavy */
	private OutputType outputType;  /* File/Console/Both  */
	private boolean activate=false;
	private SystemType sysType;    /* Agent/Msg/Traffic/GIS/Monitoring / ZoneAnalizer */
	private static Date dt = null;
	private StandardEntity self;
	private String path;
//	private SOSWorldModel world = null;
	private File file=null;
    private FileWriter fw=null;
//    private int id;

     /* Default System for Constructor is 'Agent' */
    public OldLogger(ModeType mode,OutputType output,StandardEntity self,boolean active){
        this.self=self;
        this.modeType=mode;
        this.outputType=output;
        this.sysType= SystemType.Agent;
        this.activate=active;
        if(activate){
            if(outputType!= OutputType.Console){
                createFilePath();
                createDirectory();
                createNewFile(getNewFileName());

            }
        }
    }
    
    //Message and other provided by Faraz
    public OldLogger(ModeType mode,OutputType output,StandardEntity self,SystemType sys,boolean active){
        this.self=self;
        this.modeType=mode;
        this.outputType=output;
        this.sysType=sys;
        this.activate=active;
        if(activate){
            if(outputType!= OutputType.Console){
                createFilePath();
                createDirectory();
                createNewFile(getNewFileName());
            }
        }
    }

    /* a special constructor for systems  */
    public OldLogger(ModeType mode,OutputType output,StandardEntity self,SystemType sys,boolean active, SOSWorldModel ds){
        this.self=self;
//        this.world = ds;
        this.modeType=mode;
        this.outputType=output;
        this.sysType=sys;
        this.activate=active;
        if(activate){
            if(outputType!= OutputType.Console){
                createFilePath();
                createDirectory();
                createNewFile(getNewFileName());

            }
        }
    }
    
    public OldLogger(ModeType mode,OutputType output,int id,SystemType sys,boolean active){
//        this.self=self;
//    	  this.id=id;
       // this.world = ds;
        this.modeType=mode;
        this.outputType=output;
        this.sysType=sys;
        this.activate=active;
        if(activate){
            if(outputType!= OutputType.Console){
                createFilePath2();
                createDirectory();
                createNewFile(Integer.valueOf(id).toString());
            }
        }
    }
    
    public void log(String str){
    	exportln(str);
    }
    
    public void log(Object obj){
    	log(obj.toString());
    }
    
    /* Writing string in a line with default Medium mode */
    public void exportln(String str){
        export(str+"\n");
    }
    
    //******************************************************
    /** Functions for writing logs  */

    /* Default mode for this function is Medium */
    public void export(String str ){
        export(str, ModeType.Medium);
    }
    /* Writing string in a line   */
    public void exportln(String str,ModeType mode){
        export(str+"\n",mode);
    }
    public void export(String str,ModeType mode){
        if(activate){
            int checking=modeType.compareTo(mode);
            if(checking>=0){
                switch(outputType){
                        case File:
                            try {
                                fw.write(str);
                                fw.flush();
                            }catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case Console:
                            System.out.print(str);
                            break;
                        case Both:
                            try {
                                fw.write(str);
                                fw.flush();
                            }catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            System.out.print(str);
                     }

            }
        }
    }
    //******************************************************
    /**In this function we does'nt check already set properties and use it directly
     *
     * @param output  directly gives us output mode
     * @param str     the string that must be written
     */
    protected void exportDirectln(OutputType output,String str){
         exportDirect(output,str+"\n");
    }
    protected void exportDirect(OutputType output,String str){
         if(activate){
             switch(output){
                        case File:
                            if(fw==null){
                                createDirectory();
                                createNewFile(self.getID()+"");
                            }
                            try {

                                fw.write(str);
                                fw.flush();
                            }catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case Console:
                            System.out.print(str);
                            break;
                        case Both:
                            try {
                                fw.write(str);
                                fw.flush();
                            }catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            System.out.println(str);
                     }

         }
    }
    //******************************************************
	/* Function for setting properties  */
	public void setMode(ModeType mode){
        this.modeType=mode;
    }
    /*to changing output mode every where in code we want */
    public void setOutput(OutputType output){
        this.outputType=output;
        if(output!= OutputType.Console){
            if(fw==null){
                createDirectory();
                createNewFile(getNewFileName());
            }
        }
    }
    //******************************************************
	/*Function for manipulate files */
    private void createDirectory(){
        File dir=new File(this.path);
        dir.mkdirs();
    }
    
    private String getNewFileName(){
        String name=null;
        if(self instanceof AmbulanceTeam){
           name="AT(";
        }else if(self instanceof FireBrigade){
           name="FB(";
        }else if(self instanceof PoliceForce){
           name="PF(";
        }else if(self instanceof AmbulanceCenter){
           name="AC(";
        }else if(self instanceof FireStation){
           name="FS(";
        }else if(self instanceof PoliceOffice){
           name="PO(";
        }else{}

        if(self!=null){
            //name="(";
            name+=self.getID()+")";
        }

        switch(sysType){
        	case GIS:
        		name+="_GIS";
        		break;
			case Sampling:
					name+="_Sampling";
					break;
    		case Voronoi:
    			name+="_Voronoi";
    			break;
    		case Iland:
    			name+="_Iland";
    			break;
			case Building:
				name+="_Building";
				break;
    		case RoadSite:
    			name+="_RoadSite";
    			break;
        	case ZoneAnalizer:
        		name+="_ZoneAnalizer";
        		break;
        	case LocalStrategyChooser:
        		name+="_LocalStrategyChooser";
        		break;
        	case GlobalStrategyChooser:
        		name+="_GlobalStrategyChooser";
        		break;
            case Monitoring:
                name+="_Monitoring";
                break;
            case MessageSend:
                name+="_MessageSend";
                break;
            case MessageReceive:
            	name+="_MessageReceive";
            	break;
            case Msg:
                name+="_MSG.log";
                break;
            case Traffic:
                name+="_Traffic";
                break;
            case Base:
                name+="_Base";
                break;
            case GraphUsage:
                name+="_GU";
                break;
            case AmbulanceDecision:
                name+="_AmbDecision";
                break;
            case AT:
                name+="_AT";
                break;
            case Clustering:
            	name+="_Clustering";
                break;
            case MapChecking:
                name+="_MapChecking";
        }
        name+=".log";
        return name;
    }

    private boolean createNewFile(String name){
//   	  System.out.println(path+name);
        file=new File(path+name);
        try{
            file.createNewFile();
            fw=new FileWriter(path+name);

        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void createFilePath2(){
		if(dt == null){
			dt = new java.util.Date();
		}
		else{
			/* doesn't change the time */
		}
		path ="Logs/"+dt.toString().substring(0,16);
		if(this.sysType==SystemType.Clustering){
			path+="/Clustering/";
		}
		else{
			path+="/Normailize/";
		}
    }
    
	private void createFilePath(){
		if(dt == null){
			dt = new java.util.Date();
		}
		else{
			/* doesn't change the time */
		}
        switch(sysType){
            case Agent:
                path ="Logs/";
                if(self instanceof AmbulanceTeam || self instanceof AmbulanceCenter){
                	//path += dt.toString().substring(0,16);
                	path+="AmbulanceTeams/";
                }
                else if(self instanceof FireBrigade || self instanceof FireStation){
//                	String st = new String(((NewFireDisasterSpace)world).getMapName());
                	//path += /*st +"-"+ */dt.toString().substring(0,16)+"/";
                    path+="FireBrigades/";
                }
                else if(self instanceof PoliceForce || self instanceof PoliceOffice){
                	//path += dt.toString().substring(0,16)+"/";
                    path+="PoliceForce/";
                }
                break;
            case GIS:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/GIS/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/GIS/";
            	}
                break;
            case Sampling:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/Sampling/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/Sampling/";
            	}
                break;
            case Voronoi:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/Voronoi/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/Voronoi/";
            	}
                break;
            case Iland:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/Iland/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/Iland/";
            	}
                break;
            case Building:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/Building/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/Building/";
            	}
                break;
            case RoadSite:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/RoadSite/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/RoadSite/";
            	}
                break;
            case MessageSend:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/Message/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/Message/MessageSend/";
            	}
                break;
            case GraphUsage:
                path="Logs/";
                path+= dt.toString().substring(0,16)+"/GraphUsage/";
                break;
            case MessageReceive:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/Message/";
//            	}
            	{
            		path+= dt.toString().substring(0,16)+"/Message/MessageReceive/";
            	}
                break;
            case ZoneAnalizer:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/ZoneAnalizer/";
//            	}
            	{
            		System.out.println("Err in LoggingSystem.java ..................................");
            	}
                break;
            case LocalStrategyChooser:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/LocalStrategyChooser/";
//            	}
            	{
            		System.out.println("Err in LoggingSystem.java ..................................");
            	}
                break;
            case GlobalStrategyChooser:
            	path="Logs/";
//            	if(self instanceof FireBrigade){
//            		path+= ((NewFireDisasterSpace)world).getMapName()+"-"+ dt.toString().substring(0,16)+"/GlobalStrategyChooser/";
//            	}
            	{
            		System.out.println("Err in LoggingSystem.java ..................................");
            	}
                break;
             case  Monitoring:
                path="Logs/"+ dt.toString().substring(0,16)+"/Monitoring/";
                break;
            case Msg:
                path="Logs/"+ dt.toString().substring(0,16)+"/MSG/";
                break;
            case Traffic:
                path="Logs/";
                //path+= dt.toString().substring(0,16);
                path+="/Traffic/";
                break;
            case Base:
                path="Logs";
                //path+=dt.toString().substring(0,16);
                path+="/Base/";
                break;
            case AmbulanceDecision:
                path="Logs/";
                //path+=dt.toString().substring(0,16);
                path+="/AmbulanceDecision/";
                break;
            case Clustering:
            	path="Logs/"+dt.toString().substring(0,16)+"/Clustering/";
                break;
            case MapChecking:
                path="Logs/";
                //path+=dt.toString().substring(0,16);
                path+="/MapChecking/";
                break;
            case AT:
                path="Logs/";
                //path+=dt.toString().substring(0,16);
                path+="/AT/";
        }
    }
    //******************************************************
    @Override
	 protected void finalize(){
        try{
            fw.close();
        }catch(IOException er){
            er.printStackTrace();
        }

    }
}
