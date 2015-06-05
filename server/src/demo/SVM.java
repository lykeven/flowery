package demo;

import java.io.File;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.ml.*;

public class SVM 
{
	public  Mat svm_trainingimages;
	public  Mat svm_trainingdata;
	public  Mat svm_traininglabels;
	public  Mat svm_classes;
	public  CvSVMParams svm_params;
	public  CvSVM classifier;
	
	public SVM()
	{
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		svm_params = new CvSVMParams();							
		svm_trainingdata = new Mat();
		svm_trainingimages = new Mat();
		svm_traininglabels = new Mat();
		svm_classes = new Mat();
	}
	

	public void init()
	{																	//参数初始化
		svm_params.set_svm_type(CvSVM.C_SVC);						//c类支持向量分类机，n类分组
		svm_params.set_kernel_type(CvSVM.POLY);				//采用线性核，备选RBF核
		svm_params.set_degree(0.5);												//内核函数（POLY）的参数degree
		svm_params.set_gamma(2);												//内核函数（POLY/ RBF/ SIGMOID）的参数
		svm_params.set_C(1);				 										//SVM类型（C_SVC/ EPS_SVR/ NU_SVR）的参数C
		svm_params.set_nu(0);														//SVM类型（NU_SVC/ ONE_CLASS/ NU_SVR）的参数 
		svm_params.set_p(0);														//SVM类型（EPS_SVR）的参数
		svm_params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER, 1000, 0.01));
																									//SVM的迭代训练过程的中止条件，
																									//解决部分受约束二次最优问题,
																									//可以指定的公差和/或最大迭代次数
	}
	
	

	public void getTrainingfiles(String path)
	{
		svm_trainingimages = new Mat();
																								//将positives下文件全部加入
		File[] files = new File(path).listFiles();
		int j=0;
		for (int i = 0; i<files.length; i++) {
			if(!files[i].isHidden())
			{									
				j=1+i/9;
				Mat img=new Mat();												//创建连续存储的img
				img.create(new Size(768,1024), CvType.CV_32FC1);
				System.out.println(files[i].getAbsolutePath());		
																								//读入不连续存储的灰度img2
		        Mat  img2 = Highgui.imread(files[i].getAbsolutePath(),0 ); 
		        img2.copyTo(img);								//将不连续存储的img2复制到连续存储的img											
		    
		        img = img.reshape(1, 1);										//特征向量以行来存储!
		        img.convertTo(img, CvType.CV_32FC1);				//转换为CV_32FC1类型
		        svm_trainingimages.push_back(img);					//加入到训练数据中
		        Mat m=Mat.ones(new Size(1, 1), CvType.CV_32FC1);

		        m=m.mul(m, j);
		        svm_classes.push_back(m);
		      
			}
	    }
	
	    svm_trainingimages.copyTo(svm_trainingdata);			//训练数据写到svm_trainingdata中
	    svm_trainingdata.convertTo(svm_trainingdata, CvType.CV_32FC1);//转换格式
	    svm_classes.copyTo(svm_traininglabels);						//标签数据写到svm_traininglabels中

	    System.out.println(svm_trainingimages.toString());
	    System.out.println(svm_trainingdata.toString());
	    System.out.println(svm_classes.toString());
	    
	    																						//创建svm：classifier
	    classifier = new CvSVM(svm_trainingdata, svm_traininglabels, new Mat(), new Mat(), svm_params);
	
	    System.out.println("支持向量个数："+classifier.get_support_vector_count());
	  
	    
	    
	}
	
	
	
	public double predict(String testPicture)
	{
		Mat sample=Highgui.imread(testPicture, 0);
	    Mat img=new Mat();										
		img.create(new Size(554,128), CvType.CV_32FC1);
        sample.copyTo(img);										
        img = img.reshape(1, 1);										//特征向量以行来存储!
        img.convertTo(img, CvType.CV_32FC1);				//转换为CV_32FC1类型
	    double ans=classifier.predict(img);
	    return ans;
	}

	
	
	public void close()
	{
		classifier.clear();
		svm_trainingdata = new Mat();
		svm_traininglabels = new Mat();
		svm_classes = new Mat();
	}
	
	
	
	
}