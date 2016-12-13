package gui.pages;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.google.common.eventbus.Subscribe;

import gui.WizardPage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import run.HiRec;
import util.MessageBus;

/**
 * @author FBM
 *
 */
public class RunPage extends WizardPage{

	private StatusMessageAppender outputTextArea;
	private ScrollPane algorithmsScrollPane;
	private GridPane algorithmsGrid;
	/**
	 * @param title
	 */
	public RunPage() {
		super("Run");
		MessageBus.getInstance().register(this);
		Logger.getRootLogger().addAppender(outputTextArea);
	}

	@Subscribe
	private void addAlgorithmComponent(final AlgorithmLevelUpdateMessage message) {
		final Parent component = new AlgorithmVisualComponent(message.getId(), message.getAlgorithmName(), message.getNumberOfFold()).getLayout();
		Platform.runLater(() -> {			
			if(algorithmsGrid==null){
				algorithmsGrid = new GridPane();
				algorithmsGrid.add(component, 0, 0);
			}else{
				algorithmsGrid.add(component, (message.getId()%2)!=0?0:1, (int) Math.ceil(message.getId()/2.)-1);
			}
			algorithmsGrid.setAlignment(Pos.CENTER);
		});
	}

	/* (non-Javadoc)
	 * @see gui.WizardPage#getContent()
	 */
	@Override
	protected Parent getContent() {
		outputTextArea= new StatusMessageAppender();
		algorithmsGrid = new GridPane();
		algorithmsScrollPane = new ScrollPane(algorithmsGrid);
		algorithmsScrollPane.setStyle("-fx-background-color:transparent;-fx-background: rgb(255, 248, 220);");
		final VBox vBox = new VBox(5.0,outputTextArea.getArea(),algorithmsScrollPane);
		return vBox;
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
}

class StatusMessageAppender extends AppenderSkeleton {
    private final TextArea textArea;

	public StatusMessageAppender() {
		this.textArea = new TextArea();
		this.textArea.setMinHeight(400);
		this.textArea.setFont(new Font("Monaco",12));
		this.textArea.setEditable(false);
		this.textArea.clear();
	}
    public void close() {
    }
    
    /**
	 * @return
	 */
	public Node getArea() {
		return textArea;
	}

	public boolean requiresLayout() {
        return false;
    }

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent event) {
		Platform.runLater(()->{
			final String prevText = textArea.getText();
			textArea.clear();
			textArea.setText(prevText+"\n"+event.getMessage().toString());
		});
	}

}
