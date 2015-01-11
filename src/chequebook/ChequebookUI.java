package chequebook;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.*;

import javax.servlet.annotation.WebServlet;

/**
 * Created by rurik
 */
@Theme("touchkit")
@Widgetset("com.vaadin.addon.touchkit.gwt.TouchKitWidgetSet")
public class ChequebookUI extends UI {
    @Override
    public void init(VaadinRequest request) {
        Person person = Bank.instance.findPerson(((VaadinServletRequest) request).getRequestURI().substring(1));
        setContent(new TabBarView() {{
            addTab(new VerticalComponentGroup() {{
                ComboBox peer = new ComboBox("User", new BeanItemContainer<>(Person.class, Bank.instance.getPersons())) {{
                    setNullSelectionAllowed(false);
                    setTextInputAllowed(false);
                    setItemCaptionPropertyId("name");
                }};
                NumberField amount = new NumberField("Amount");
                addComponents(peer, amount);
            }}, "Add", FontAwesome.PLUS);
            addTab(new Table() {{
                setSizeFull();
                setContainerDataSource(new BeanItemContainer<>(Transaction.class, person.transactions));
                setVisibleColumns("created", "peer", "amount", "comment");
                setColumnHeaders("Created", "Peer", "Amount", "Comment");
            }}, "Transactions", FontAwesome.LIST);
            addTab(new VerticalComponentGroup() {{
                addComponent(new TextField("Name", new MethodProperty(person, "name")));
                if (person.admin) {
                    addComponent(new Button("Add user", clickEvent -> {
                        Person p = Bank.instance.addPerson();
                        JavaScript.eval("window.open('" + p.key + "', '_blank');");
                    }));
                }
            }}, "Settings", FontAwesome.GEAR);
        }});
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(productionMode = true, ui = ChequebookUI.class)
    public static class Servlet extends TouchKitServlet {
    }
}
