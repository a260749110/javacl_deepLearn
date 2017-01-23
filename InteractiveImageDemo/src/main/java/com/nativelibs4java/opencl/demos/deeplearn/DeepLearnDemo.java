package com.nativelibs4java.opencl.demos.deeplearn;

import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.chooseFile;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.createLinkLabel;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.isMac;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.readTextResource;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.textArea;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.traceToHTML;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.traceToString;
import static com.nativelibs4java.opencl.demos.interactiveimage.Utils.withTitle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.bridj.Platform;

import com.draw.DrawForm;
import com.draw.DrawForm.PaintData;
import com.draw.DrawForm.PaintText;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLException;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLPlatform;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.opencl.demos.SetupUtils;
import com.ochafik.io.ReadText;
import com.ochafik.io.WriteText;
import com.ochafik.swing.UndoRedoUtils;
import com.ochafik.swing.syntaxcoloring.CCTokenMarker;
import com.ochafik.swing.syntaxcoloring.JEditTextArea;

public class DeepLearnDemo extends JPanel {
	JSplitPane imgSrcSplitPane, imgsSplitPane;
	JLabel  resultImgLab, instructionsLabel, timeLabel, progressLabel;
	JScrollPane origImgScroll, resultImgScroll;

	JEditTextArea sourceTextArea;
	JComboBox devicesCombo, examplesCombo;
	// JTextArea sourceTextArea;
	private DrawForm drawForm=new DrawForm();
	JButton runButton;
	BufferedImage image;
	JProgressBar progressBar;

	JComponent[] toDisable;
	File lastOpenedFile;
	private DeepLearnPackage deepLearnPackage = new DeepLearnPackage(2236);
	static final String RUN_ACTION = "run", SAVE_ACTION = "save";
	File persistentFile = new File(
			new File(new File(System.getProperty("user.home"), ".javacl"), getClass().getSimpleName()), "Test.cl");

	boolean load() {
		if (!persistentFile.exists())
			return false;

		sourceTextArea.setText(ReadText.readText(persistentFile));
		return true;
	}

	void save() {
		try {
			WriteText.writeText(sourceTextArea.getText(), persistentFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, traceToHTML(ex), "Failed to save file", JOptionPane.ERROR_MESSAGE);
		}
	}

	class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			save();
		}
	}

	private boolean runFlag = false;

	void run() {
		save();
		try {
			final BufferedImage bufferedImage = getImage();
			if (bufferedImage == null)
				return;

			// Could just be this : final CLContext context =
			// JavaCL.createBestContext();
			final CLContext context = getContext();
			if (context == null)
				return;

//			final Point initialViewPosition = origImgScroll.getViewport().getViewPosition();

			for (JComponent c : toDisable)
				c.setEnabled(false);
			resultImgLab.setText(null);
			resultIcon(null);
			resultImgLab.setToolTipText(null);

			timeLabel.setVisible(false);
			progressBar.setIndeterminate(true);
			progressBar.setVisible(true);
			setProgress("Initializing...");

			final long[] elapsedTimeNanos = new long[] { -1L };
			new Thread() {
				public void run() {
					runFlag = true;
					while (runFlag) {

						try {
							setProgress("Creating OpenCL queue...");
							CLQueue queue = context.createDefaultQueue();
							setProgress("Compiling program...");
							CLProgram program = context.createProgram(sourceTextArea.getText());
							CLKernel[] kernels = program.createKernels();
							if (kernels.length == 0)
								throw new RuntimeException(
										"No kernels found in the source code ! (please mark a function with __kernel)");

							setProgress("Creating OpenCL images...");

							float[] f = { 0.2f, 0.5f, 0.8f };
							FloatBuffer buff = FloatBuffer.wrap(f);

							// cldouble.write(queue, 0, 1,9, false,
							// eventsToWaitFor);

							long startTimeNanos = System.nanoTime();
							CLEvent lastEvent = null;

							for (CLKernel kernel : kernels) {
								setProgress("Running kernel '" + kernel.getFunctionName() + "'...");
								try {
									
									deepLearnPackage.initDeepLearnData();
									deepLearnPackage.setCLKernel(kernel, context);
//									deepLearnPackage.tryCalculate(0);

									lastEvent = kernel.enqueueNDRange(queue, new int[] { Config.CalculaSize,1},
											lastEvent);
								} catch (CLException ex) {
									throw new RuntimeException("Error occurred while running kernel '"
											+ kernel.getFunctionName() + "': " + ex, ex);
								}
							}
							lastEvent.waitFor();
							elapsedTimeNanos[0] = System.nanoTime() - startTimeNanos;
							deepLearnPackage.readResult(queue, lastEvent);
							// Pointer<Float>
							// fp=cldoubleOut.read(queue,lastEvent);
							//
							// lastEvent.waitFor();
							// List<Float> outList=fp.asList();
							//
							// for (int i = 0; i < outList.size(); i++) {
							// System.err.println("i:"+i+"
							// out:"+outList.get(i));
							// }
							//
							for (CLKernel kernel : kernels)
								kernel.release();
							program.release();
							queue.release();

						} catch (Exception ex) {
							ex.printStackTrace();
							resultError(ex);
						} finally {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									setProgress(null);
									PaintData data=new PaintData();
									data.dataList=deepLearnPackage.dataEndList;
									data.color=Color.CYAN;
									PaintData dataTurnover=new PaintData();
									dataTurnover.dataList=deepLearnPackage.dataTurnoverList;
									dataTurnover.color=Color.orange;
									PaintData dataResult=new PaintData();
									dataResult.dataList=deepLearnPackage.calculateResult;
									dataResult.color=Color.red;
									PaintData dataSuccess=new PaintData();
									dataSuccess.dataList=deepLearnPackage.successList;
									dataSuccess.color=Color.green;
									PaintData line=new PaintData();
									line.dataList=deepLearnPackage.minLine;
									line.color=Color.GRAY;
									PaintText pt=new PaintText();
									pt.text=deepLearnPackage.dates;
									pt.distance=30;
									pt.color=Color.BLACK;
									drawForm.auctoScal=true;
									drawForm.setPaintText(pt);
									drawForm.setData(dataResult,dataSuccess,line,data,dataTurnover);
									
									for (JComponent c : toDisable)
										c.setEnabled(true);
									progressBar.setIndeterminate(false);
									progressBar.setVisible(false);
									if (elapsedTimeNanos[0] >= 0) {
										timeLabel.setText(
												"Completed in " + (elapsedTimeNanos[0] / 1000000.0) + " msecs");
										timeLabel.setVisible(true);
									}
								}
							});
						}
					}
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			resultError(ex);
		}
	}

	class RunAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (runFlag == false) {
				run();
			} else {
				runFlag = false;
			}
		}
	}

	String runKeyStroke = "F5";

	int spacing = 10;

	class Example {
		public Example(String caption, String fileName) {
			this.fileName = fileName;
			this.caption = caption;
		}

		public final String fileName, caption;

		@Override
		public String toString() {
			return caption;
		}

	}

	public DeepLearnDemo() {
		super(new BorderLayout());

		devicesCombo = new JComboBox();
		List<CLDevice> devices = new ArrayList<CLDevice>();
		try {
			for (CLPlatform platform : JavaCL.listPlatforms()) {
				for (CLDevice device : platform.listAllDevices(true)) {
					devicesCombo.addItem(device);
					devices.add(device);
				}
			}
			if (!devices.isEmpty())
				devicesCombo.setSelectedItem(
						CLPlatform.getBestDevice(Arrays.asList(CLPlatform.DeviceFeature.MaxComputeUnits), devices));
		} catch (Exception ex) {
			ex.printStackTrace();
			devicesCombo.setToolTipText(traceToHTML(ex));
		}
		if (devices.isEmpty()) {
			devicesCombo.addItem("No OpenCL Device detected");
		}

		examplesCombo = new JComboBox();
		examplesCombo.addItem("Examples...");
		{
			final String signature = "__kernel void transform(__global read_only image2d inputImage, __global write_only image2d outputImage)";

			examplesCombo.setToolTipText("Kernel samples in the form of :\n'" + signature + "'");
			for (Example example : new Example[] {
					// "Blur",
					new Example("deeplearn", "deeplearn") }) {
				examplesCombo.addItem(example);
			}
			examplesCombo.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					String t = sourceTextArea.getText();
					if (t.trim().length() > 0)
						t = t + "\n";
					sourceTextArea.setText(t + "__kernel " + signature
							+ " {\n\tint x = get_global_id(0), y = get_global_id(1);\n\t// write here\n}");
				}
			});
			examplesCombo.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					Object selection = examplesCombo.getSelectedItem();
					if (selection instanceof Example) {
						loadExample(((Example) selection).fileName);
						examplesCombo.setSelectedIndex(0);
					}
				}
			});
		}

		JPanel srcPanel = new JPanel(new BorderLayout());
		sourceTextArea = textArea(new CCTokenMarker());
		

		runButton = new JButton("Run (" + runKeyStroke + ")");
		{
			Box toolbar = Box.createHorizontalBox();
			for (JComponent c : new JComponent[] { examplesCombo, runButton, devicesCombo })
				c.setMaximumSize(c.getPreferredSize());

			runButton.putClientProperty("JButton.buttonType", "bevel");
			examplesCombo.putClientProperty("JComboBox.isPopDown", Boolean.TRUE);
			devicesCombo.putClientProperty("JComboBox.isPopDown", Boolean.TRUE);

			toolbar.add(examplesCombo);
			toolbar.add(runButton);
			toolbar.add(devicesCombo);
			toolbar.add(createLinkLabel("Khronos OpenCL Documentation",
					"http://www.khronos.org/registry/cl/sdk/1.0/docs/man/xhtml/"));
			toolbar.add(Box.createHorizontalStrut(spacing));
			toolbar.add(createLinkLabel("JavaCL FAQ", "http://code.google.com/p/javacl/wiki/FAQ"));
			toolbar.add(Box.createHorizontalStrut(spacing));
			toolbar.add(Box.createHorizontalGlue());
			toolbar.add(progressLabel = new JLabel());
			toolbar.add(Box.createHorizontalStrut(spacing));
			toolbar.add(progressBar = new JProgressBar());
			progressBar.putClientProperty("JProgressBar.style", "circular");
			toolbar.add(timeLabel = new JLabel());
			progressBar.setMaximumSize(progressBar.getPreferredSize());
			progressLabel.setVisible(false);
			progressBar.setVisible(false);
			timeLabel.setVisible(false);
			srcPanel.add("Center", withTitle("Image transformation kernel source code", sourceTextArea));
			srcPanel.add("South", toolbar);
//			srcPanel.add("Center", new JSplitPane(	JSplitPane.VERTICAL_SPLIT ,withTitle("Image transformation kernel source code", sourceTextArea),toolbar) );
		}
		drawForm.setAutoscrolls(true);
	
		origImgScroll = new JScrollPane(drawForm);
		resultImgScroll = new JScrollPane(resultImgLab = new JLabel());

//		for (JScrollPane sp : Arrays.asList(origImgScroll, resultImgScroll)) {
//			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		}
//		origImgScroll.setMinimumSize(new Dimension(100, 700));
//		origImgScroll.setSize(new Dimension(drawForm.rw,1000));
		resultVertScrollModel = resultImgScroll.getVerticalScrollBar().getModel();
		resultHorzScrollModel = resultImgScroll.getHorizontalScrollBar().getModel();

//		origImgLab.setDropTarget(new DropTarget(origImgLab, DnDConstants.ACTION_COPY, imgDropTargetListener));
		
		JSplitPane jSplitPane;
		add("Center",jSplitPane=new JSplitPane(	JSplitPane.VERTICAL_SPLIT ,origImgScroll,srcPanel));
	
		jSplitPane.setAutoscrolls(true);
		runButton.addActionListener(new RunAction());

		toDisable = new JComponent[] { examplesCombo, runButton, devicesCombo, sourceTextArea,
				// origImgLab,
				// resultImgLab
		};

		UndoRedoUtils.registerNewUndoManager(sourceTextArea, sourceTextArea.getDocument());
		for (JComponent focusable : Arrays.asList(sourceTextArea, examplesCombo, devicesCombo, runButton)) {
			InputMap im = focusable.getInputMap();
			ActionMap am = focusable.getActionMap();
			im.put(KeyStroke.getKeyStroke(runKeyStroke), RUN_ACTION);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK),
					SAVE_ACTION);
			am.put(RUN_ACTION, new RunAction());
			am.put(SAVE_ACTION, new SaveAction());
		}
	}

	protected DropTargetListener imgDropTargetListener = new DropTargetListener() {

		public void dragEnter(DropTargetDragEvent dtde) {
			try {
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
						|| dtde.isDataFlavorSupported(DataFlavor.stringFlavor)
						|| dtde.isDataFlavorSupported(DataFlavor.imageFlavor))
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
				else
					dtde.rejectDrag();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		}

		public void dragExit(DropTargetEvent dte) {
		}

		public void dragOver(DropTargetDragEvent dtde) {
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		public void drop(DropTargetDropEvent dtde) {
			try {
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					java.util.List<File> files = (java.util.List<File>) dtde.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					if (files != null && !files.isEmpty()) {
						readImage(files.get(0).toURI().toURL());
					}
					dtde.dropComplete(true);
				} else if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					try {
						dtde.acceptDrop(DnDConstants.ACTION_COPY);
						readImage(new URL((String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor)));
						dtde.dropComplete(true);
						return;
					} catch (MalformedURLException ex) {
					}
				} else if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					Image image = (Image) dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
					if (image instanceof BufferedImage)
						setImage((BufferedImage) image);
					dtde.dropComplete(true);
					return;
				}
				dtde.rejectDrop();
			} catch (Exception ex) {
//				origImgLab.setToolTipText(traceToHTML(ex));
			}
		}
	};

	BufferedImage getImage() {
		if (image == null)
			chooseImage();
		return image;
	}

	void readImage(URL url) {
		try {
			setImage(null);

			InputStream in = url.openStream();
			if (in == null)
				return;

			lastOpenedFile = new File(url.getFile());
			setImage(ImageIO.read(in));

			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
//			origImgLab.setText(traceToHTML(ex));
		}
	}

	void setImage(BufferedImage image) {
		this.image = image;
//		origImgLab.setText(null);
		origIcon(image == null ? null : new ImageIcon(image));
	}

	void readImageResource(String name) {
		readImage(Platform.getClassLoader(getClass()).getResource("images/" + name));
	}

	void chooseImage() {
		try {
			File f = chooseFile(lastOpenedFile, true);
			if (f == null)
				return;

			readImage(f.toURI().toURL());
		} catch (Exception ex) {
			ex.printStackTrace();
//			origImgLab.setText(traceToHTML(ex));
		}
	}

	String getOutputFormat(File file) {
		if (file != null) {
			String s = file.getName().toLowerCase();
			if (s.matches(".*?\\.jpe?g"))
				return "jpeg";
			for (String ex : new String[] { "png", "gif", "tiff", "pnm", "pbm" })
				if (s.matches(".*?\\." + ex))
					return ex;
		}
		return "png";
	}

	static Pattern fileExtRx = Pattern.compile("(.*?)(\\.[^.]+)?");

	CLContext getContext() {
		Object selection = devicesCombo.getSelectedItem();
		if (!(selection instanceof CLDevice))
			return null;

		CLDevice device = (CLDevice) selection;
		CLContext context = JavaCL.createContext(null, device);
		return context;
	}

	void setProgress(final String caption) {
		System.out.println(caption);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// progressLabel.setVisible(caption != null);
				// progressLabel.setText(caption);
				if (!isMac()) {
					progressBar.setStringPainted(caption != null);
					progressBar.setString(caption);
				}
				progressBar.setToolTipText(caption);
			}
		});
	}

	BoundedRangeModel resultVertScrollModel, resultHorzScrollModel;

	void resultIcon(Icon icon) {
//		if (icon == null) {
//			resultImgScroll.getVerticalScrollBar().setModel(resultVertScrollModel);
//			resultImgScroll.getHorizontalScrollBar().setModel(resultHorzScrollModel);
//		} else {
//			resultImgScroll.getVerticalScrollBar().setModel(origImgScroll.getVerticalScrollBar().getModel());
//			resultImgScroll.getHorizontalScrollBar().setModel(origImgScroll.getHorizontalScrollBar().getModel());
//		}
//		resultImgLab.setIcon(icon);
	}

	void origIcon(Icon icon) {
//		origImgLab.setIcon(icon);
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				JScrollBar bar;
//				BoundedRangeModel model;
//				model = (bar = origImgScroll.getVerticalScrollBar()).getModel();
//				model.setValue((model.getMinimum() + model.getMaximum()) / 2);
//				model = (bar = origImgScroll.getHorizontalScrollBar()).getModel();
//				model.setValue((model.getMinimum() + model.getMaximum()) / 2);
//
//			}
//		});
	}

	void resultError(Exception ex) {
		String html = traceToHTML(ex);
		resultIcon(null);
		resultImgLab.setText(html);
		resultImgLab.setToolTipText(html);
	}

	void loadExample(String fileName) {
		try {
			String s = readTextResource("examples/" + fileName + ".cl");
			sourceTextArea.setText(s);
			sourceTextArea.setCaretPosition(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			sourceTextArea.setText("Failed to load example '" + fileName + "' :\n" + traceToString(ex));
		}
	}

	public static void main(String[] args) {
		SetupUtils.failWithDownloadProposalsIfOpenCLNotAvailable();

		JFrame f = new JFrame("JavaCL's Interactive Image Transform Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DeepLearnDemo demo = new DeepLearnDemo();
		f.getContentPane().add("Center", demo);
		f.setSize(1200, 800);
		f.setVisible(true);

		demo.getContext();
		demo.readImageResource("lena.jpg");
		if (!demo.load())
			demo.loadExample("Convolution");
	}

}
