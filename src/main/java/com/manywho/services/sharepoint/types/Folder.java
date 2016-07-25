package com.manywho.services.sharepoint.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Folder extends AbstractType {
    public final static String NAME = "Folder";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementBindingCollection getBindings() {
        return new TypeElementBindingCollection() {{
            add(new TypeElementBinding(NAME, "Details about a folder", NAME, new TypeElementPropertyBindingCollection() {{
                add(new TypeElementPropertyBinding("ID", "ID"));
                add(new TypeElementPropertyBinding("Name", "Name"));
                add(new TypeElementPropertyBinding("Description", "Description"));
                add(new TypeElementPropertyBinding("Files", "Files"));
                add(new TypeElementPropertyBinding("Created At", "Created At"));
                add(new TypeElementPropertyBinding("Modified At", "Modified At"));
            }}));
        }};
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
            add(new TypeElementProperty("Name", ContentType.String));
            add(new TypeElementProperty("Description", ContentType.String));
            add(new TypeElementProperty("Files", ContentType.List, File.NAME));
            add(new TypeElementProperty("Created At", ContentType.DateTime));
            add(new TypeElementProperty("Modified At", ContentType.DateTime));
        }};
    }
}
