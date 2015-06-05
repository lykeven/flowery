package demo;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.*;

import com.sina.sae.util.SaeUserInfo;

public class opencvtest {

private int number=35+1;	
private int kind=7;
private int numberofonekind=5;
private int numberoffeature=554;
private int ID[];
private int flowerNumber[];
private String flowerName[];
private String fileAddress[];
private String MaxscoreFlower[];
private String description[];
private double score[];
private double totalscore[];

private SVM mysiftsvm;


private String retstr;
private String Path="./data//";
private String svmModelPath=Path+"trainmodel7_5_t";
static String fname[]={"","rose","chrysanthemum","Gardenia","dandelion",
	"daffodil","catharanthus roseus","sunflower","tulip","lotus"};
String url = "jdbc:sqlserver://localhost:1433; DatabaseName=mydata";
String sSQL="select * from myappdata ";
String username="user";
String password="password";

public int getKind() {
	return kind;
}


public void setKind(int kind) {
	this.kind = kind;
}


public String[] getFlowerName() {
	return flowerName;
}


public void setFlowerName(String[] flowerName) {
	this.flowerName = flowerName;
}

public String  getMaxscoreFlower() {
	return MaxscoreFlower[0];
}

public String getretstr()
{
	return retstr;
}

public opencvtest()
{
	
}


public opencvtest(String testPicture,int flagsql,int flagtrain)
{
	System.load("./data/opencv_java249.dll");
	
	if(flagsql==1)
	sqlConnect();
	else
	sqlConnectOnline();
	
	int ans=0;
	if(flagtrain==1)
	ans=(int)useSvm(testPicture);
	else
	ans=(int)useSvmbyfile(testPicture);
	
	String svmflower=flowerName[ans*numberofonekind];
	System.out.println("svm计算结果： 第"+ans+"种花："+svmflower);
	MaxscoreFlower[0]=svmflower;
	
	retstr=description[ans*numberofonekind];
	/*
	compute(testPicture);
	System.out.println("颜色比较计算结果：");
	System.out.println("第1种花："+MaxscoreFlower[0]);
	System.out.println("第2种花："+MaxscoreFlower[1]);
	System.out.println("第3种花："+MaxscoreFlower[2]);
	
	judgeName(svmflower);
	*/
	
}


void judgeidentification(String path,int index)
{
	System.load("./data/opencv_java249.dll");
	sqlConnect();
	int j=0;
	File[] files = new File(path).listFiles();
	System.out.println("实际结果：  第"+index+"种花"+flowerName[index*numberofonekind]);
	int result []= new int [kind+1];
	mysiftsvm=new SVM();
	mysiftsvm.init();
	mysiftsvm.classifier=new CvSVM();
	mysiftsvm.classifier.load(svmModelPath);
	//double ans=predict(testPicture);
	for (int i = 0; i<files.length; i++) {
		if(!files[i].isHidden())
		{	
	       
	        int ans=(int)predict(files[i].getAbsolutePath());
	        result[ans]++;
	        String svmflower=flowerName[ans*numberofonekind];
	    	System.out.print("svm计算结果： 第  "+(i+1)+"张图片的结果是第  "+ans+"种花："+svmflower);
	
	    	if(ans==index)
	    	{
	    		j++;
	    		System.out.println("	符合情况");
	    	}
	    	else
	    		System.out.println("	不符情况");
	    	
	    	
		}
    }

	
	
	
	for(int i=1;i<=kind;i++)
	{
	double percent=(double)result[i]/(double)(files.length);
	System.out.println(flowerName[i*numberofonekind]+"识别率："+percent);
	}
}



//siftsvm的训练结果与颜色比较的结果的综合

public void judgeName(String svmflower)
{
	if(svmflower==MaxscoreFlower[0])
	{
		
	}
	else if(svmflower==MaxscoreFlower[1])
	{
		String temp=MaxscoreFlower[0];
		MaxscoreFlower[0]=MaxscoreFlower[1];
		MaxscoreFlower[1]=temp;
		
	}
	else if(svmflower==MaxscoreFlower[2])
	{
		String temp=MaxscoreFlower[1];
		MaxscoreFlower[1]=MaxscoreFlower[2];
		MaxscoreFlower[2]=temp;
	} else
	{
		MaxscoreFlower[2]=MaxscoreFlower[1];
		MaxscoreFlower[1]=MaxscoreFlower[0];
		MaxscoreFlower[0]=svmflower;
	}
   System.out.println("最终匹配结果");
   System.out.println("第一名："+MaxscoreFlower[0]);
   System.out.println("第二名："+MaxscoreFlower[1]);
   System.out.println("第三名："+MaxscoreFlower[2]);
}
	
//训练测试数据并预测
public double useSvm(String testPicture)
{
	mysiftsvm=new SVM();
	mysiftsvm.init();
	getTrainingfiles();
	double ans=predict(testPicture);
	
	return ans;
}

//通过已训练出的数据进行预测
public double useSvmbyfile(String testPicture)
{
	mysiftsvm=new SVM();
	mysiftsvm.init();
	mysiftsvm.classifier=new CvSVM();
	mysiftsvm.classifier.load(svmModelPath);
	double ans=predict(testPicture);
	
	return ans;
}

//获得训练图片sift特征矩阵并加入到训练机中
public void getTrainingfiles()
{
	mysiftsvm.svm_trainingimages = new Mat();	
	int j=0;
	for (int i = 0; i<number-1; i++)
	{
											
			j=1+i/numberofonekind;
			Mat img=new Mat();												//创建连续存储的img
			img.create(new Size(128,numberoffeature), CvType.CV_32FC1);
			Mat img2=new Mat();																				//读入不连续存储的灰度img2
			img2 = sift(fileAddress[i+1]); 
			
			//Imgproc.resize(img2, img2, new Size(128,numberoffeature));
			System.out.println(fileAddress[i+1]+":  width="+img2.width()+"      height:"+img2.height());		
	        img2.copyTo(img);								//将不连续存储的img2复制到连续存储的img											
	        img = img.reshape(1, 1);										//特征向量以行来存储!
	        img.convertTo(img, CvType.CV_32FC1);				//转换为CV_32FC1类型
	        mysiftsvm.svm_trainingimages.push_back(img);					//加入到训练数据中
	        
	        Mat m=Mat.ones(new Size(1, 1), CvType.CV_32FC1);
	        m=m.mul(m, j);
	        mysiftsvm.svm_classes.push_back(m);
	        
	        img.release();
	        img2.release();
	        m.release();
    }

    mysiftsvm.svm_trainingimages.copyTo(mysiftsvm.svm_trainingdata);			//训练数据写到svm_trainingdata中
    mysiftsvm.svm_trainingdata.convertTo(mysiftsvm.svm_trainingdata, CvType.CV_32FC1);//转换格式
    mysiftsvm.svm_classes.copyTo(mysiftsvm.svm_traininglabels);						//标签数据写到svm_traininglabels中

    System.out.println(mysiftsvm.svm_trainingimages.toString());
    System.out.println(mysiftsvm.svm_trainingdata.toString());
    System.out.println(mysiftsvm.svm_classes.toString());
    System.out.println(mysiftsvm.svm_traininglabels.toString());

    
    																						//创建svm：classifier
    mysiftsvm.classifier = new CvSVM(mysiftsvm.svm_trainingdata, mysiftsvm.svm_traininglabels, new Mat(), new Mat(), mysiftsvm.svm_params);
 
    System.out.println("支持向量个数："+mysiftsvm.classifier.get_support_vector_count());

    
    mysiftsvm.classifier.save(svmModelPath);
    
} 

//预测测试图片的种类
public double predict(String testPicture)
{
	Mat sample=sift(testPicture);
	//Imgproc.resize(sample, sample, new Size(128,numberoffeature));
    Mat img=new Mat();										
	img.create(new Size(128,numberoffeature), CvType.CV_32FC1);
    sample.copyTo(img);										
    img = img.reshape(1, 1);										//特征向量以行来存储!
    img.convertTo(img, CvType.CV_32FC1);				//转换为CV_32FC1类型
    double ans=mysiftsvm.classifier.predict(img);
    return ans;
}


public Mat preprocess(String path)
{
	 //读取图像，不改变图像的原始信息
    Mat m = Highgui.imread(path,Highgui.CV_LOAD_IMAGE_COLOR);
   
    //将图片转换成灰度图片
    Mat gray = new Mat(m.size(),CvType.CV_8UC1);
    
    Imgproc.cvtColor(m,gray,Imgproc.COLOR_RGB2GRAY);

    //计算灰度直方图
    List<Mat> images = new ArrayList<Mat>();
    images.add(gray);

    MatOfInt channels= new MatOfInt(0);
    MatOfInt histSize = new MatOfInt(256);
    MatOfFloat ranges= new MatOfFloat(0,256);
    Mat hist = new Mat();
    Imgproc.calcHist(images, channels, new Mat(), hist, histSize, ranges);
    
    //mat求和
    System.out.println(Core.sumElems(hist));

    Mat binaryMat = new Mat(m.size(),CvType.CV_8UC1);
    
    Imgproc.adaptiveThreshold(gray, binaryMat, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5,4);
  
	Mat cedge = new Mat(m.size(),CvType.CV_8UC3);
	Mat edge = new Mat(m.size(),CvType.CV_8UC1);
	
	Size size = new Size(3, 3);
    Imgproc.blur(gray,edge,size);//平滑处理
    double threshold = 1.0;
    Imgproc.Canny(gray, edge, threshold, threshold*3, 3, false);
    m.copyTo(cedge, edge);
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();    
    Point contoursPoint = new Point(0, 0);
    Imgproc.findContours(binaryMat, contours, new Mat(),Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, contoursPoint);
    System.out.println(contours.size());
    
    Mat pOutlineImage = new Mat(m.size(),CvType.CV_8UC1);
    Mat img = new Mat(m.size(),CvType.CV_8UC3);
    m.copyTo(img, pOutlineImage);
    Highgui.imwrite("./data//testpictures//1.jpg",cedge);
    return binaryMat;
}

public void compute(String testPicture)
{
	for (int i=1;i<number;i++)
	{
		score[i]=compareHist(fileAddress[i],testPicture);
		//System.out.println("第"+i+"张图片:"+"颜色得分： "+score[i]);
	}
	totalscore =new double[kind+1];
	for(int i=1;i<=kind;i++)
	{
		totalscore[i]=0;
		int index =(i-1)*kind;
		int j;
		for(j=1;j<=numberofonekind;j++)
			totalscore[i]+=score[index+j];

	}
	for (int i=1;i<=kind;i++)
		System.out.println("第"+i+"种花总分"+totalscore[i]);
	for (int i=1;i<=kind;i++)
	{
		for(int j=i+1;j<=kind;j++)
			if(totalscore[i]<totalscore[j])
			{
				double temp=totalscore[j];
				totalscore[j]=totalscore[i];
				totalscore[i]=temp;
				
				String temp2=flowerName[j*numberofonekind];
				flowerName[j*numberofonekind]=flowerName[i*numberofonekind];
				flowerName[i*numberofonekind]=temp2;
				
			}
	}
	

	
	MaxscoreFlower[0]=flowerName[1*numberofonekind];
	MaxscoreFlower[1]=flowerName[2*numberofonekind];
	MaxscoreFlower[2]=flowerName[3*numberofonekind];
	
	
}

//sift特征矩阵的比较
public double sift(String trainPicture,String testPicture)
{
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

    Mat test_mat1  = Highgui.imread(trainPicture);
    Mat test_mat2 = Highgui.imread(testPicture);
 
    Mat desc1 = new Mat();
    Mat desc2 = new Mat();
    
    FeatureDetector fd1 = FeatureDetector.create(FeatureDetector.SIFT);//创建特征描述子
    FeatureDetector fd2 = FeatureDetector.create(FeatureDetector.SIFT);//创建特征描述子
    MatOfKeyPoint mkp1 =new MatOfKeyPoint();
    MatOfKeyPoint mkp2 =new MatOfKeyPoint();
    
    fd1.detect(test_mat1, mkp1);
    fd2.detect(test_mat2, mkp2);
    DescriptorExtractor de1 = DescriptorExtractor.create(DescriptorExtractor.SIFT);
    DescriptorExtractor de2 = DescriptorExtractor.create(DescriptorExtractor.SIFT);
    de1.compute(test_mat1,mkp1,desc1 );//提取sift特征
    de2.compute(test_mat2,mkp2,desc2 );//提取sift特征
    
    MatOfDMatch matches  = new MatOfDMatch();
    DescriptorMatcher dm =  DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
    dm.match(desc1, desc2, matches);

	int  size=matches.cols()*matches.rows();
    double si=size;
	DMatch []darray =new DMatch [size];
	darray=matches.toArray();
	DMatch d=new DMatch();

    double max_dist = 0;
	double min_dist = 100;
	for (int i = 0; i<size; i++)
	{
		double dist = darray[i].distance;
		//System.out.println(dist);
	
		if (dist < min_dist) min_dist = dist;
		if (dist > max_dist) max_dist = dist;
	}

	//筛选出较好的匹配点
	List goodMatches=new ArrayList();
	for (int i = 0; i<size; i++)
	{
		if (darray[i].distance <=0.31 * max_dist)
		{
			goodMatches.add(darray[i]);
		}
	}
	double ans=goodMatches.size()/si;
	
	if(goodMatches!=null)
	System.out.println( "matches:   "+size+"     goodMatcher:   "+goodMatches.size() +"       percent1:     "+ans); 
	test_mat1.release();
	test_mat2.release();
	desc1.release();
	desc2.release();
	goodMatches.clear();
	return ans;
}

	
//提取sift特征矩阵
public Mat sift(String path)
{
    Mat test_mat1  = Highgui.imread(path);
    //System.out.println(test_mat1.channels());
    //Imgproc.resize(test_mat1, test_mat1, new Size(128,numberoffeature));
    Mat desc1 = new Mat();
    
    FeatureDetector fd1 = FeatureDetector.create(FeatureDetector.SIFT);//创建特征描述子
    //FeatureDetector fd1 = FeatureDetector.create(FeatureDetector.
    MatOfKeyPoint mkp1 =new MatOfKeyPoint();
    
    fd1.detect(test_mat1, mkp1);
    DescriptorExtractor de1 = DescriptorExtractor.create(DescriptorExtractor.OPPONENT_SIFT);
   
    de1.compute(test_mat1,mkp1,desc1 );//提取sift特征
    test_mat1.release();
	mkp1.release();
	//System.out.println(":  width="+desc1.width()+"      height:"+desc1.height());
	Mat ans=new Mat();
	//System.out.println(desc1.channels());
	ans=tran(desc1,numberoffeature);
	return ans;
}

public  Mat tran(Mat img2,int stanheight)
{
	
	int height=img2.height();
	int width=img2.width();

	Mat img=new Mat(new Size(width,stanheight),CvType.CV_32FC1);
	
	if(height>=stanheight)
	{
	for(int k=0;k<stanheight;k++)
		for(int l=0;l<img2.width();l++)
		{
			double a=img2.get(k, l)[0];
			float b[]={(float) a};
			img.put(k, l, b);
		}
	}
	else
	{
		int loopnumber=stanheight/height;
		for(int m=0;m<loopnumber;m++)
		{
			for(int k=0;k<height;k++)
			{
				for(int l=0;l<img2.width();l++)
				{
					double a=img2.get(k, l)[0];
					float b[]={(float) a};
					img.put(k+height*m, l, b);
				}
			}
		}
		for(int k=0;k<stanheight-loopnumber*height;k++)
		{
			for(int l=0;l<img2.width();l++)
			{
				double a=img2.get(k, l)[0];
				float b[]={(float) a};
				img.put(k+loopnumber*height, l, b);
			}
		}
	}

	return img;
}



//颜色直方图比较
public double compareHist(String trainPicture,String testPicture)
{																					
	 //System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
     List<Mat> imgaesList1=new ArrayList<>();
     List<Mat> imgaesList2=new ArrayList<>();
     new Highgui();
		Mat img1 = Highgui.imread(trainPicture);
     new Highgui();
		Mat img2 = Highgui.imread(testPicture);  
     imgaesList1.add(img1);
     imgaesList2.add(img2);
     
     int channelArray[]={0,1,2};
     MatOfInt channels=new MatOfInt(channelArray);
     Mat hist1=new Mat();
     Mat hist2=new Mat();
     MatOfInt histSize=new MatOfInt(256, 256, 256);

     MatOfFloat ranges=new MatOfFloat(0.0f,255.0f, 0.0f, 255.0f, 0.0f, 255.0f);
     
     //MatOfInt histSize=new MatOfInt(1, 1, 1)
     //MatOfFloat ranges=new MatOfFloat(0.0f,1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
     
     Imgproc.calcHist(imgaesList1, channels,new Mat(), hist1, histSize, ranges);
     Imgproc.calcHist(imgaesList2, channels,new Mat(), hist2, histSize, ranges);
     //Core.normalize(hist1, hist1);
     //Core.normalize(hist2, hist2);
     
     double cmp1 = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
     cmp1 = Math.abs(cmp1);
     //double cmp2 = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_INTERSECT);
     //cmp2 = Math.abs(cmp2);
     //double ans = Math.sqrt(cmp1*cmp2);
     img1.release();
     img2.release();
     hist1.release();
     hist2.release();
     imgaesList1.clear();
     imgaesList2.clear();
     return cmp1;
}
	

public Mat compareHist(String path)
{
	 System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
     List<Mat> imgaesList1=new ArrayList<>();
     new Highgui();
	 Mat img1 = Highgui.imread(path);
     imgaesList1.add(img1);
     int channelArray[]={0,1,2};
     MatOfInt channels=new MatOfInt(channelArray);
     Mat hist1 =new Mat();
     //hist1.create(new Size(256,256), CvType.CV_32FC1);
     MatOfInt histSize=new MatOfInt(256, 256, 256);
     MatOfFloat ranges=new MatOfFloat(0.0f,255.0f, 0.0f, 255.0f, 0.0f, 255.0f);
     Imgproc.calcHist(imgaesList1, channels,new Mat(), hist1, histSize, ranges);
     
     //hist1, 3维矩阵，元素类型为32FC1, 尺寸256*256，
     Core.normalize(hist1, hist1);
     Imgproc.resize(hist1, hist1, new Size(256,256));
     hist1.convertTo(hist1, CvType.CV_32FC1);	
     img1.release(); 
     imgaesList1.clear();
 	/*
 	System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
 	IplImage srcImg=cvLoadImage("pictures//rose1.jpg");
 	cvNamedWindow("Test");
 	cvShowImage("Test",srcImg);
 	cvWaitKey(0);
 	cvReleaseImage(srcImg);
 	cvDestroyWindow("Text");
 	System.load("./data/opencv_java249.dll");
	Mat m=new Mat(new Size(2,2),CvType.CV_32FC1);
	float data00[]={1},data01[]={2},data10[]={3},data11[]={4};
	m.put(0, 0, data00);m.put(0, 1, data01);m.put(1, 0, data10);m.put(1, 1, data11);
	double a=m.get(0, 0)[0],b=m.get(0, 1)[0],c=m.get(1, 0)[0],d=m.get(1, 1)[0];
	System.out.println(m.dump());
	System.out.println(a+"  "+b+"   "+c+"   "+d);
 	*/
     return hist1;
}
	

//连接本地sqlserver数据库
public void sqlConnect()
{
	 try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    ResultSet rs = null; //操作结果
	    try {
	    	
	    	 
			 Connection con = DriverManager.getConnection(url,username, password);
		      Statement statement = con.createStatement();
		      rs = statement.executeQuery(sSQL);
		      
	    	  
		      int count=0;
		      ID =new int[number];
		      flowerName =new String[number];
		      flowerNumber =new int[number];
		      fileAddress =new String[number];
		      score =new double [number];
		      description=new String [number];
		      MaxscoreFlower =new String[3];
		      while(rs.next())
		      {
		    	  count++;
		    	  ID[count]=count;
		    	  flowerName[count]=rs.getString("flower name"); 
		    	  fileAddress[count]=Path+rs.getString("file address"); 
		    	  description[count]=rs.getString("file description");
		    	  if(count>=number-1)
		    		  break;
		    	  //System.out.println(fileAddress[count]);
		      }
		   statement.close();
		       	con.close(); 
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
}



//连接sae数据库
public void sqlConnectOnline()
{
	    ResultSet rs = null; //操作结果
	    try {

	    	String dbUrl = "jdbc:mysql://w.rdc.sae.sina.com.cn:3307/app_identification";  
	    	String Username=SaeUserInfo.getAccessKey();
	    	String Password=SaeUserInfo.getSecretKey();
	    	String Driver="com.mysql.jdbc.Driver";
	    	try {
				Class.forName(Driver).newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	Connection con = DriverManager.getConnection(dbUrl,Username,Password);
	    	//Connection con = DriverManager.getConnection("SAE_MYSQL_DB","SAE_MYSQL_USER", "SAE_MYSQL_PASS");
	    	Statement statement = con.createStatement();
	    	rs = statement.executeQuery("select * from myappdata");
  
		      int count=0;
		      ID =new int[number];
		      flowerName =new String[number];
		      flowerNumber =new int[number];
		      fileAddress =new String[number];
		      score =new double [number];
		      while(rs.next())
		      {
		    	  count++;
		    	  ID[count]=count;
		    	  flowerName[count]=rs.getString("flower name"); 
		    	  fileAddress[count]=rs.getString("file address"); 
		    	  
		      }
		   statement.close();
		       	con.close(); 
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
}



	//构造函数三个参量:
	//第一个表示要测试图片的文件路径及文件名
	
	//第二个表示要进行连接的数据库的类型，1表示本地数据库
	//必要时需要重新更改下数据库名，用户名，密码,
	//该参数不是1时，表示连接到sae数据库
	
	//第三个参数表示要进行的操作
	//1，表示通过获取所有数据库中的图片进行训练并预测
	//该参数不是1时，表示通过已得到的训练数据进行预测

public static void choose(int c)
{
	if(c==1)
	{
		opencvtest t=new opencvtest("./data//testpictures//3.jpg",1,1);
		String s=t.getMaxscoreFlower();
		System.out.println(s);
	}
	else
	{
		opencvtest t1= new opencvtest();
		//for(int i=1;i<=t1.kind;i++)
		//t1.judgeidentification("./data//testpictures//test"+fname[i],i);
		t1.judgeidentification("./data//testpictures//testchrysanthemum",2);
	}
}


public static void main(String []args)
{
	
	//choose(1);
	choose(2);

}




}
