package chequebook;

import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by rurik
 */
@Theme("touchkit")
@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
public class ChequebookUI extends UI {
    PersonTable personTable = new PersonTable();
    NewTransactionForm form = new NewTransactionForm();
    Person me;
    TabBarView tabBarView;

    @Override
    public void init(VaadinRequest request) {
        try {
            String ctx = ((VaadinServletRequest) request).getRequestURI().substring(1);
            if (Bank.instance.isAdmin(ctx)) {
                setContent(new VerticalComponentGroup() {{
                    TextField username = new TextField("New user name");
                    addComponent(username);
                    addComponent(new Button("Create user", clickEvent -> {
                        if (!Strings.isNullOrEmpty(username.getValue())) {
                            open(Bank.instance.addPerson(username.getValue()).getKey());
                        }
                    }));
                    addComponent(new Button("Regenerate admin key", clickEvent -> {
                        open(Bank.instance.generateAdminKey());
                    }));
                }});
            } else {
                me = Bank.instance.findPerson(ctx);
                tabBarView = new TabBarView() {{
                    addTab(personTable, "All", FontAwesome.LIST_OL);
                    addTab(form, "Add", FontAwesome.PLUS);
                    addTab(new TransactionTable(), "My transactions", FontAwesome.TABLE);
                }};
                setContent(tabBarView);
            }
        } catch (Exception e) {
            Notification.show("Not authenticated", Notification.Type.ERROR_MESSAGE);
            close();
        }
    }

    private void open(String key) {
        JavaScript.eval("window.open('" + key + "', '_blank');");
    }

    private class PersonTable extends Table {
        {
            setSizeFull();
            setContainerDataSource(new BeanItemContainer<>(Person.class, Bank.instance.getPersons()));
            setVisibleColumns("name", "balance");
            setColumnHeaders("Name", "Balance");
            setSortContainerPropertyId("balance");
            addItemClickListener(event -> {
                tabBarView.setSelectedTab(form);
                form.setValue((Person) event.getItemId());
            });
        }
    }

    private class NewTransactionForm extends VerticalComponentGroup {
        ComboBox peer = new ComboBox("User", new BeanItemContainer<>(Person.class, Bank.instance.getPersons())) {{
            setNullSelectionAllowed(false);
            setTextInputAllowed(false);
            setItemCaptionPropertyId("name");
        }};
        NumberField amount = new NumberField("Amount");
        TextField comment = new TextField("Comment");
        Button send = new Button("Send", event -> {
            BigDecimal m = parse(amount.getValue());
            Person p = (Person) peer.getValue();
            if (p != null && m.compareTo(BigDecimal.ZERO) > 0) {
                Bank.instance.addTransaction(Instant.now(), me, p, m, comment.getValue());
                setValue(null);
                tabBarView.setSelectedTab(personTable);
                personTable.refreshRowCache();
            }
        });

        private BigDecimal parse(String value) {
            try {
                return new BigDecimal(value);
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        {
            send.setWidth("100%");
            addComponents(peer, amount, comment, send);
        }

        public void setValue(Person person) {
            peer.setValue(person);
            amount.setValue(null);
            comment.setValue("");
        }
    }

    private class TransactionTable extends Table {
        {
            setSizeFull();
            setContainerDataSource(new BeanItemContainer<>(Transaction.class, me.getTransactions()));
            setVisibleColumns("created", "peerName", "amount", "comment");
            setColumnHeaders("Created", "Peer", "Amount", "Comment");
            setSortContainerPropertyId("created");
            setSortAscending(false);
            setConverter("created", new CellConverter<Instant>(Instant.class) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

                @Override
                protected String convert(Instant instant) {
                    return fmt.format(instant);
                }
            });
        }
    }

    @WebServlet("/*")
    @VaadinServletConfiguration(productionMode = true, ui = ChequebookUI.class)
    public static class Servlet extends TouchKitServlet {
    }

}
