package org.oulipo.browser.wizards;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.people.Account;
import org.oulipo.browser.controls.OulipoTable;
import org.oulipo.rdf.model.User;
import org.oulipo.streams.IRI;

public final class NewAccountWizard {

	private final BrowserContext ctx;

	private final OulipoTable table = new OulipoTable(300, 350);

	public NewAccountWizard(BrowserContext ctx) {
		this.ctx = ctx;
	}

	private WizardPane accountCreated() {
		WizardPane page2 = new WizardPane() {

			@Override
			public void onEnteringPage(Wizard wizard) {
				super.onEnteringPage(wizard);
				try {
					Account newAccount = ctx.getAccountManager().newAccount();

					setContentText(
							"Congratulations. You've created an account with public key " + newAccount.publicKey);
					ctx.getAccountManager().login(newAccount, "http://localhost:4567/auth");

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void onExitingPage(Wizard wizard) {
				super.onExitingPage(wizard);
			}

		};
		page2.setMinHeight(350);
		return page2;

	}

	private WizardPane createAccount() {
		WizardPane pane = new WizardPane();
		pane.setContentText("Let's create an account");
		pane.setMinHeight(350);
		return pane;
	}

	private WizardPane createUser() {
		WizardPane page3 = new WizardPane() {
			@Override
			public void onEnteringPage(Wizard wizard) {
				super.onEnteringPage(wizard);
				this.setHeight(350);
				setContent(table);
			}

			@Override
			public void onExitingPage(Wizard wizard) {
				super.onExitingPage(wizard);
				String xandle = table.getValue("Xandle");
				User user = new User();
				user.xandle = xandle;
				user.publicKey = table.getValue("Public Key");
				user.subject = new IRI(table.getValue("Tumbler Address"));
				ctx.setUserName(user.subject.value, xandle);
				wizard.getSettings().put("user", user);
			}

		};
		page3.setMinHeight(350);
		return page3;
	}

	public void startWizard() {
		WizardPane page1 = createAccount();
		WizardPane page2 = accountCreated();
		WizardPane page3 = createUser();

		Wizard wizard = new Wizard();
		wizard.setFlow(new LinearFlow(page1, page2, page3));

		wizard.showAndWait().ifPresent(result -> {
			// if (result == ButtonType.FINISH) {
			System.out.println("Wizard finished, settings: " + wizard.getSettings());
			// }
		});
	}
}
