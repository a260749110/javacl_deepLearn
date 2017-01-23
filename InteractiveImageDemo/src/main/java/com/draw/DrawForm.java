package com.draw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class DrawForm extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	   private static final int sx = 50;//小方格宽度
	    private static final int sy = 50;//小方格高度
	    private static final int w = 40;
	    public static final int rw = 2000;
	    private static final int rh = 600;
	    private PaintText paintText;
	    private Graphics jg;
	    
	    
	    
	    private Color rectColor = new Color(0xf5f5f5);
	    
	    /**
	     * DrawSee构造方法
	     */
	    public DrawForm() {
	    	setPreferredSize(new Dimension(rw+sx*4, 1000));
	    	
	    }
	   
	    @Override
	    public void paint(Graphics g) {
	    	// TODO Auto-generated method stub
	    	paintComponents( g);
	    }
	    private List<PaintData> paintDatas=new ArrayList<>();
	    public void setData(PaintData...datas )
	    {
	    	paintDatas.clear();
	    	for (PaintData d:datas) {
	    		paintDatas.add(d);
			}
	    	
	    
	    	this.repaint();
	    
	    }
	    private Color baseColor=Color.black;
	    
	    
	    private double scale=1.0f;
	    private int maxSize;
	    private void reafreshScale()
	    {
	    	double max=0;
	    	maxSize=0;
	    	double rhd=(double)rh/2;
	    	for (PaintData data:paintDatas) {
	    		int size=0;
				for(Double f:data.dataList)
				{
					double absf=Math.abs(f);
					if(data.maxData<absf)
					{
						data.maxData=absf;
						data.scaleY=(rhd/absf);
						
						
					}
					size++;
				}
				if(size>maxSize)
				{
					maxSize=size;
				}
			}
	    }
	    public  boolean auctoScal=false;
	    public void paintComponents(Graphics g) {
	        try {
	            
	        
	            // 设置线条颜色为红色
	            g.setColor(baseColor);
	            
	            // 绘制外层矩形框
	         
	            g.drawLine(sx,sy,sx,rh+sy);
	            //Y
	            g.drawLine(sx/2,rh/2+sy,rw,rh/2+sy);
	            reafreshScale();
	            double cellSize=((double )rw)/(maxSize+1);
	            double startX=0+sx;
	            if(!auctoScal)
	            {
	            double scale=99999999f;
	        	for(PaintData pd:  paintDatas )
				{
	        		if(scale>pd.scaleY)
				{
					scale=pd.scaleY;
				}
				}
	        	for(PaintData pd:  paintDatas )
				{
	        		pd.scaleY=scale;
				}
	            }
	            for (int i = 0; i < maxSize-1; i++) {
	            	int y = 0;
					for(PaintData pd:  paintDatas )
					{
					
						g.setColor(pd.color);
						if(pd.startSize<=i&&(pd.startSize+i)<pd.dataList.length-1&&(pd.startSize+i)>=0)
						{
							
							g.drawLine((int)startX,rh/2- (int) (pd.dataList[i]*pd.scaleY)+sy, (int)(startX+cellSize),rh/2-  (int)(pd.dataList[i+1]*pd.scaleY)+sy);
							 y=rh/2-  (int)(pd.dataList[i+1]*pd.scaleY)+sy;
						}
				
						
					}
					if((paintText!=null)&&(paintText.distance==0||(i%paintText.distance==0))&&(paintText.startSize<=i&&(paintText.startSize+i)<paintText.text.length-1&&(paintText.startSize+i)>=0))
					{
				        Graphics2D g2 = (Graphics2D)g ;
//				        g2.translate((int)startX, rh/2+sy);
//				        g2.rotate(1);
						g2.setColor(paintText.color);
						g2.drawString(paintText.text[i], (int)startX,y);
//						 g2.rotate(-1);
//						  g2.translate(-(int)startX, -(rh/2+sy));
					}
					startX+=cellSize;
				}
	            
	          
	            
	            /* 绘制水平10个，垂直10个方格。
	             * 即水平方向9条线，垂直方向9条线，
	             * 外围四周4条线已经画过了，不需要再画。
	             * 同时内部64个方格填写数字。
	             */
//	            
//	            for(int i = 1; i < 5; i ++) {
//	                // 绘制第i条竖直线
//	                g.drawLine(sx + (i * w), sy, sx + (i * w), sy + rw);
//	                
//	                // 绘制第i条水平线
//	                g.drawLine(sx, sy + (i * w), sx + rw, sy + (i * w));
//	                
//	                // 填写第i行从第1个方格到第8个方格里面的数字（方格序号从0开始）
//	                for(int j = 0; j < 10; j ++) {
//	                    //drawString(g, j, i);                    
//	                }
//	            }
//	            for(int i = 5; i < 10; i ++) {
//	                g.setColor(Color.green);
//	                // 绘制第i条竖直线
//	                g.drawLine(sx + (i * w), sy, sx + (i * w), sy + rw);
//	                
//	                // 绘制第i条水平线
//	                g.drawLine(sx, sy + (i * w), sx + rw, sy + (i * w));
//	                
//	                // 填写第i行从第1个方格到第8个方格里面的数字（方格序号从0开始）
//	                for(int j = 0; j < 10; j ++) {
//	                    //drawString(g, j, i);                    
//	                }
//	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    public PaintText getPaintText() {
			return paintText;
		}

		public void setPaintText(PaintText paintText) {
			this.paintText = paintText;
		}
		public static class PaintData
	    {
	    	public int startSize=0;
	    	public double scaleY=-1f;
	    	public double[] dataList;
	    	public Color color=Color.BLACK;
	    	public double maxData;
	    }
	    public static class PaintText
	    {
	    public  String[] text;
	    public int startSize=0;


    	public Color color=Color.BLACK;
    	public int distance;
	    }
}
