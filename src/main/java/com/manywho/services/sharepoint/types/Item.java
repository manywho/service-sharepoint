package com.manywho.services.sharepoint.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Item extends AbstractType{
    public final static String NAME = "Item";

    @Override
    public TypeElementBindingCollection getBindings() {
        return new TypeElementBindingCollection() {{
            add(new TypeElementBinding(NAME, "Details about a Item", NAME, new TypeElementPropertyBindingCollection() {{
                add(new TypeElementPropertyBinding("ID", "ID"));
                add(new TypeElementPropertyBinding("Created Date Time", "Created Date Time"));
                add(new TypeElementPropertyBinding("Last Modified Date Time", "Last Modified Date Time"));
                add(new TypeElementPropertyBinding("e Tag", "e Tag"));
                add(new TypeElementPropertyBinding("Web URL", "Web URL"));
                add(new TypeElementPropertyBinding("List Item ID", "List Item Id"));
                add(new TypeElementPropertyBinding("Site ID", "Site ID"));
                add(new TypeElementPropertyBinding("List ID", "List ID"));
            }}));
        }};
    }

    @Override
    public String getDeveloperName() {
        return Item.NAME;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
            add(new TypeElementProperty("Created Date Time", ContentType.DateTime));
            add(new TypeElementProperty("Last Modified Date Time", ContentType.DateTime));
            add(new TypeElementProperty("e Tag", ContentType.String));
            add(new TypeElementProperty("Web URL", ContentType.String));
            add(new TypeElementProperty("List Item ID", ContentType.String));
            add(new TypeElementProperty("Site ID", ContentType.String));
            add(new TypeElementProperty("List ID", ContentType.String));
        }};
    }
}
