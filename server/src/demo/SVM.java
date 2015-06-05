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
	{																	//������ʼ��
		svm_params.set_svm_type(CvSVM.C_SVC);						//c��֧�������������n�����
		svm_params.set_kernel_type(CvSVM.POLY);				//�������Ժˣ���ѡRBF��
		svm_params.set_degree(0.5);												//�ں˺�����POLY���Ĳ���degree
		svm_params.set_gamma(2);												//�ں˺�����POLY/ RBF/ SIGMOID���Ĳ���
		svm_params.set_C(1);				 										//SVM���ͣ�C_SVC/ EPS_SVR/ NU_SVR���Ĳ���C
		svm_params.set_nu(0);														//SVM���ͣ�NU_SVC/ ONE_CLASS/ NU_SVR���Ĳ��� 
		svm_params.set_p(0);														//SVM���ͣ�EPS_SVR���Ĳ���
		svm_params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER, 1000, 0.01));
																									//SVM�ĵ���ѵ�����̵���ֹ������
																									//���������Լ��������������,
																									//����ָ���Ĺ����/������������
	}
	
	

	public void getTrainingfiles(String path)
	{
		svm_trainingimages = new Mat();
																								//��positives���ļ�ȫ������
		File[] files = new File(path).listFiles();
		int j=0;
		for (int i = 0; i<files.length; i++) {
			if(!files[i].isHidden())
			{									
				j=1+i/9;
				Mat img=new Mat();												//���������洢��img
				img.create(new Size(768,1024), CvType.CV_32FC1);
				System.out.println(files[i].getAbsolutePath());		
																								//���벻�����洢�ĻҶ�img2
		        Mat  img2 = Highgui.imread(files[i].getAbsolutePath(),0 ); 
		        img2.copyTo(img);								//���������洢��img2���Ƶ������洢��img											
		    
		        img = img.reshape(1, 1);										//���������������洢!
		        img.convertTo(img, CvType.CV_32FC1);				//ת��ΪCV_32FC1����
		        svm_trainingimages.push_back(img);					//���뵽ѵ��������
		        Mat m=Mat.ones(new Size(1, 1), CvType.CV_32FC1);

		        m=m.mul(m, j);
		        svm_classes.push_back(m);
		      
			}
	    }
	
	    svm_trainingimages.copyTo(svm_trainingdata);			//ѵ������д��svm_trainingdata��
	    svm_trainingdata.convertTo(svm_trainingdata, CvType.CV_32FC1);//ת����ʽ
	    svm_classes.copyTo(svm_traininglabels);						//��ǩ����д��svm_traininglabels��

	    System.out.println(svm_trainingimages.toString());
	    System.out.println(svm_trainingdata.toString());
	    System.out.println(svm_classes.toString());
	    
	    																						//����svm��classifier
	    classifier = new CvSVM(svm_trainingdata, svm_traininglabels, new Mat(), new Mat(), svm_params);
	
	    System.out.println("֧������������"+classifier.get_support_vector_count());
	  
	    
	    
	}
	
	
	
	public double predict(String testPicture)
	{
		Mat sample=Highgui.imread(testPicture, 0);
	    Mat img=new Mat();										
		img.create(new Size(554,128), CvType.CV_32FC1);
        sample.copyTo(img);										
        img = img.reshape(1, 1);										//���������������洢!
        img.convertTo(img, CvType.CV_32FC1);				//ת��ΪCV_32FC1����
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