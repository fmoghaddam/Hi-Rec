package gui.pages;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.google.common.eventbus.Subscribe;

import gui.WizardPage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import run.HiRec;
import util.MessageBus;

/**
 * @author Farshad Moghaddam
 *
 */
public class RunPage extends WizardPage implements Consumers {

	private TextArea outputTextArea;
	private ScrollPane algorithmsScrollPane;
	private GridPane algorithmsGrid;
	/**
	 * @param title
	 */
	public RunPage() {
		super("Run");
		System.setOut(new PrintStream(new StreamCapturers(this, System.out)));
		MessageBus.getInstance().register(this);

	}

	@Subscribe
	private void addAlgorithmComponent(final AlgorithmLevelUpdateMessage message) {
		final Parent c = new AlgorithmVisualComponent(message.getId(), message.getAlgorithmName(), message.getNumberOfFold()).getLayout();
		Platform.runLater(() -> {			
			if(algorithmsGrid==null){
				algorithmsGrid = new GridPane();
				algorithmsGrid.add(c, 0, 0);
			}else{
				algorithmsGrid.add(c, (message.getId()%2)!=0?0:1, (int) Math.ceil(message.getId()/2.)-1);
			}
			algorithmsGrid.setAlignment(Pos.CENTER);
		});
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	protected Parent getContent() {
		outputTextArea = new TextArea();
		outputTextArea.setMinHeight(400);
		algorithmsGrid = new GridPane();
		algorithmsScrollPane = new ScrollPane(algorithmsGrid);
		algorithmsScrollPane.setStyle("-fx-background-color:transparent;-fx-background: rgb(241,255,242);");
		final VBox vBox = new VBox(5.0,outputTextArea,algorithmsScrollPane);
		return vBox;
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#validate()
	 */
	@Override
	protected boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#shouldHideNext()
	 */
	@Override
	protected boolean shouldHideNext() {
		return true;
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#shouldHideCancel()
	 */
	@Override
	protected boolean shouldHideCancel() {
		return false;
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#shouldHideBack()
	 */
	@Override
	protected boolean shouldHideBack() {
		return true;
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#reloadIfNeeded()
	 */
	@Override
	protected void reloadIfNeeded() {
		HiRec.execute();
	}

	/* (non-Javadoc)
	 * @see gui.pages.Consumers#appendText(java.lang.String)
	 */
	@Override
	public void appendText(String text) {
		Platform.runLater( () -> {
			final String prevText = outputTextArea.getText();
			outputTextArea.setText(prevText+text);
		});
	}

}

interface Consumers {        
	public void appendText(String text);        
}

class StreamCapturers extends OutputStream {

	private StringBuilder buffer;
	private Consumers consumer;
	private PrintStream old;

	public StreamCapturers(Consumers consumer, PrintStream old) {
		buffer = new StringBuilder(128);
		this.old = old;
		this.consumer = consumer;
	}

	@Override
	public void write(int b) throws IOException {
		char c = (char) b;
		String value = Character.toString(c);
		buffer.append(value);
		if (value.equals("\n")) {
			consumer.appendText(buffer.toString());
			buffer.delete(0, buffer.length());
		}
		old.print(c);
	}        
}    
