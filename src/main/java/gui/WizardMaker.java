package gui;

import gui.pages.AlgorithmWizard;
import gui.pages.CrossValidationWizard;
import gui.pages.DataSetWizard;
import gui.pages.FirstPage;
import gui.pages.GeneralFeatureWizard;
import gui.pages.ReviewWizard;
import javafx.stage.Stage;

class WizardMaker extends Wizard {

	final Stage owner;

	public WizardMaker(Stage owner) {
		super(new FirstPage(), new DataSetWizard(), new CrossValidationWizard(), new GeneralFeatureWizard(),
				new AlgorithmWizard(), new ReviewWizard());
		this.owner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Wizard#finish()
	 */
	@Override
	public void finish() {
		owner.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Wizard#cancel()
	 */
	@Override
	public void cancel() {
		owner.close();
	}
}
