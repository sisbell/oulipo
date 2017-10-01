package org.oulipo.browser.wizards;

import java.io.IOException;

import org.controlsfx.control.HyperlinkLabel;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;
import org.oulipo.browser.api.BrowserContext;
import org.oulipo.browser.api.people.Account;
import org.oulipo.browser.controls.OulipoTable;
import org.oulipo.client.services.TumblerService;
import org.oulipo.net.IRI;
import org.oulipo.net.TumblerAddress;
import org.oulipo.resources.model.Document;
import org.oulipo.resources.model.User;
import org.oulipo.storage.StorageException;

import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class NewAccountWizard {

	private final BrowserContext ctx;

	private final OulipoTable table = new OulipoTable(300, 350);

	private TumblerService tumblerService;

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
				try {
					tumblerService = new TumblerService(ctx.getDocuverseService());
				} catch (StorageException e) {
					e.printStackTrace();
				}

				super.onEnteringPage(wizard);
				this.setHeight(350);
				setContent(table);
				try {
					tumblerService.newUser("ted://1.44", new retrofit2.Callback<User>() {

						@Override
						public void onFailure(Call<User> arg0, Throwable arg1) {

						}

						@Override
						public void onResponse(Call<User> arg0, Response<User> arg1) {
							User user = arg1.body();
							// OulipoTable table = new OulipoTable(300, 350)
							Platform.runLater(() -> {
								table.addMaterialEditText("Tumbler Address", user.resourceId.value, false)
										.addMaterialEditText("Public Key", user.publicKey, false)
										.addMaterialEditText("Xandle", user.xandle);

								// setContent(table);
							});
						}

					});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void onExitingPage(Wizard wizard) {
				super.onExitingPage(wizard);
				String xandle = table.getValue("Xandle");
				User user = new User();
				user.xandle = xandle;
				user.publicKey = table.getValue("Public Key");
				user.resourceId = new IRI(table.getValue("Tumbler Address"));
				ctx.setUserName(user.resourceId.value, xandle);
				wizard.getSettings().put("user", user);

				try {
					tumblerService.createOrUpdateUser(user, new Callback<User>() {

						@Override
						public void onFailure(Call<User> arg0, Throwable arg1) {
							arg1.printStackTrace();
						}

						@Override
						public void onResponse(Call<User> arg0, Response<User> arg1) {
						}

					});
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		};
		page3.setMinHeight(350);
		return page3;
	}

	public void startWizard() {
		WizardPane page1 = createAccount();
		WizardPane page2 = accountCreated();
		WizardPane page3 = createUser();
		WizardPane page4 = new WizardPane() {

			@Override
			public void onEnteringPage(Wizard wizard) {
				super.onEnteringPage(wizard);
				User user = (User) wizard.getSettings().get("user");
				try {
					tumblerService.newDocument(user.resourceId.value, new Callback<Document>() {

						@Override
						public void onFailure(Call<Document> arg0, Throwable arg1) {

						}

						@Override
						public void onResponse(Call<Document> arg0, Response<Document> arg1) {
							Document doc = arg1.body();
							HyperlinkLabel label = new HyperlinkLabel(
									"We've create your first document [" + doc.resourceId.value + "]");
							label.setOnAction(event -> {
								Hyperlink link = (Hyperlink) event.getSource();
								String address = link == null ? "" : link.getText();
								try {
									TumblerAddress tumbler = TumblerAddress.create(address);
									ctx.getTabManager().addTabWithAddressBar(address, tumbler.toTumblerFields());
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
							Platform.runLater(() -> {
								setContent(label);
							});

						}

					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		page4.setContentText("");
		page4.setMinHeight(350);
		// auto create first doc and show to user, user clicks and it opens document tab

		Wizard wizard = new Wizard();
		wizard.setFlow(new LinearFlow(page1, page2, page3, page4));

		wizard.showAndWait().ifPresent(result -> {
			// if (result == ButtonType.FINISH) {
			System.out.println("Wizard finished, settings: " + wizard.getSettings());
			// }
		});

		/*
		 * OulipoTab tab = new OulipoTab(newAccount.xandle);
		 * 
		 * ctx.getTabManager().add(tab); ctx.getTabManager().selectTab(tab);
		 * 
		 * } catch (IOException | StorageException e1) { e1.printStackTrace(); }
		 */

	}
}
