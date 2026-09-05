# Phase 4b: Vaadin 8 → Flow 24 conversion guide

Target: Vaadin (Flow) 24.10.9, Java 17, jakarta.servlet 6. Module: `Validation-Manager-Web`.
Every file converted must COMPILE (`mvn -pl Validation-Manager-Web compile`).

## Import rewrites (mechanical)

| Vaadin 8 | Flow 24 |
|---|---|
| `com.vaadin.ui.*` | `com.vaadin.flow.component.*` (most names match: Button, TextField, VerticalLayout, HorizontalLayout, FormLayout, CheckBox, PasswordField, TextArea, ComboBox, Grid, ...) |
| `com.vaadin.ui.Label` | `com.vaadin.flow.component.html.Label` is a `<label>` element — use `com.vaadin.flow.component.html.Span` for text display |
| `com.vaadin.ui.Window` | `com.vaadin.flow.component.dialog.Dialog` |
| `com.vaadin.ui.Panel` | `com.vaadin.flow.component.orderedlayout.Scroller` or plain layout + caption via `Span` |
| `com.vaadin.data.HasValue` | `com.vaadin.flow.component.HasValue` |
| `com.vaadin.data.Binder` | `com.vaadin.flow.data.binder.Binder` |
| `com.vaadin.data.provider.ListDataProvider` | `com.vaadin.flow.data.provider.ListDataProvider` |
| `com.vaadin.ui.UI.getCurrent()` | `com.vaadin.flow.component.UI.getCurrent()` |
| `com.vaadin.icons.VaadinIcons` | `com.vaadin.flow.component.icon.VaadinIcon` + `Icon` component |
| `com.vaadin.annotations.Title` | `@PageTitle` (flow-html-components) |
| `com.vaadin.annotations.Push` | `@Push` from `com.vaadin.flow.component.page` ( Atenna: `flow-push` is built into Flow 24 via ATMOSPHERE — no extra dep) |
| `com.vaadin.server.VaadinServlet` | `com.vaadin.flow.server.VaadinServlet` |
| `com.vaadin.server.ThemeResource` | `com.vaadin.flow.server.StreamResource` or classpath `StreamResource`/`FileDownloadResource` |
| `com.vaadin.server.ExternalResource` | anchor href / `Image` with `setSrc(url)` |
| `com.vaadin.server.VaadinService.getCurrentRequest()` | `VaadinService.getCurrent().getCurrentRequest()` (rarely needed) |
| `javax.servlet.*` | `jakarta.servlet.*` (already jakarta.servlet-api 6.0.0) |

## Semantic changes (not mechanical)

1. **Label vs Span**: v8 `Label` = value-bearing text; Flow `Label` = HTML `<label for>`. Use `Span` for captions/text.
2. **Window**: v8 windows are positioned; Flow `Dialog` is modal-centered. `window.center()` is gone. `setModal(true)` default.
3. **Grid**:
   - `grid.setItems(collection)` same.
   - Column: `addColumn(new TextRenderer<>(item -> ...))` or `ComponentRenderer<>` (native now — componentrenderer addon is gone).
   - `setRowHeight`, header captions: `column.setHeader("...")` not `setCaption`.
   - Selection: `grid.asSingleSelect().getValue()` / `addValueListener` (v8: `addSelectionListener`).
4. **TreeGrid**: same as phase 3 (TreeData/TreeDataProvider). `expand(T...)`, `collapse(T...)`.
5. **Binder**: `bindInstanceFields(this)` or explicit `forField(...).bind(getter, setter)`. `setBean` + `readBean`/`writeBean` semantics same as v8 Binder (phase-3 already migrated BeanFieldGroup→Binder, so this is mostly a package swap).
6. **ValueChangeEvent**: v8 `Property.ValueChangeEvent` → Flow `HasValue.ValueChangeEvent<E>`; lambdas `e -> ...` mostly unchanged.
7. **Notification**: `com.vaadin.flow.component.notification.Notification` — `show(String)` static exists.
8. **UI.getCurrent().access(...)**: same concept, `UI.getCurrent().access(() -> ...)`; `push()` same.
9. **Upload**: `easyuploads.MultiFileUpload` → Flow `Upload` component + `MultiFileReceiver` lambda. Keep behavior: save to temp dir, notify.
10. **messagebox addon** → `ConfirmDialog` from `com.vaadin.flow.component.confirmdialog.ConfirmDialog` (in vaadin-core). Map OK/CANCEL buttons.
11. **contextmenu addon** → `com.vaadin.flow.component.contextmenu.ContextMenu` (in vaadin-core): `contextMenu.setTarget(component)`, `addItem("text", e -> ...)`.
12. **BorderLayout addon** → compose with HorizontalLayout/VerticalLayout/FlexLayout (NORTH/SOUTH/CENTER/EAST/WEST).
13. **TreeTableCheckBox / TreeNavigator**: no Flow equivalents; reimplement with TreeGrid + CheckboxRenderer if trivially needed, or drop the feature if only used in dead UI.
14. **TableExport**: tableexport-for-vaadin addon REMOVED. ExcelExport call sites → CSV via `GridDataAdapter`-style helper or drop with TODO comment. Keep button visible but export to CSV using `StreamResource` + Apache POI (already a dep) — simplest: write workbook to `ByteArrayInputStream`, `new StreamResource(name, () -> new ByteArrayInputStream(bytes))`, `Anchor` or `UI.getCurrent().getPage().open(...)`.
15. **pdfviewer/imageviewer/jfreechartwrapper/vizcomponent/gridutil/indeterminatecheckbox**: PDF/Image: render via `Image` + `StreamResource` (byte[]/file-backed). JFreeChart: render chart to PNG bytes (`ChartUtilities.encodeAsPNG(chart.createBufferedImage(...))`) into `Image` via `StreamResource`. vizcomponent (workflow graph) → show text-based fallback with TODO comment; gridutil GridCellFilter → native `Grid.setColumnFilters` not available — implement simple per-column TextField filter row or drop filter UI.
16. **wizards-for-vaadin (teemu)**: NO Flow port. Replace with a small internal `FlowWizard` implementation: a `VerticalLayout` with `StepsHeader` (span counter), current step component, Back/Next/Finish/Cancel Buttons, `WizardStep` interface:
```java
public interface FlowWizardStep {
    Component getContent();
    String getCaption();
    boolean onAdvance();      // same as v8 semantics
    boolean onBack();
}
```
and `FlowWizard` with `addStep`, `addListener`-like callbacks (cancelled/completed/stepSetChanged/stepActivated) so existing step classes port with minimal change. Place in `...web/component/wizard/`.
17. **TabSheet** → `com.vaadin.flow.component.tabs.Tabs` + per-tab content switcher (`tab.addSelectedChangeListener` + show/hide panels).
18. **MenuBar**: `com.vaadin.flow.component.menubar.MenuBar` — `addItem("x", item -> item.getSubMenu().addItem(...))`.
19. **{@link UI} init**: `ValidationManagerUI extends UI` → class extends `com.vaadin.flow.component.applayout.AppLayout`-style? NO — keep it simple: extend `com.vaadin.flow.component.orderedlayout.VerticalLayout` implementing `VMUI`, registered via `@Route("")` + router layout. `enter()` replaces `init()`/`detach()`. `setContent()` → `add(...)`; `@Push` annotation if needed.
20. **web.xml**: replace VaadinServlet block with Flow's:
```xml
<servlet>
    <servlet-name>Fronend</servlet-name>
    <servlet-class>com.vaadin.flow.server.VaadinServlet</servlet-class>
    <init-param><param-name>frontend.url.es5</param-name></init-param>
</servlet>
<servlet-mapping><servlet-name>Fronend</servlet-name><url-pattern>/*</url-pattern></servlet-mapping>
```
plus remove sun-jaxws.xml and JAX-WS listener if the web service is dropped.
21. **Theme**: v8 valo/vmtheme → Flow Lumo. `@Theme(value = Lumo.class, ...)` on the route layout or `VaadinServlet` init-param. Custom SCSS is dropped for now (TODO later).
22. **Navigator**: v8 Navigator → Flow `@Route` navigation (RouterLink / `UI.getCurrent().navigate(X.class)`).

## Ground rules

- Match surrounding style: Javadoc comments, license headers, `private static final long serialVersionUID` only on classes that had it.
- Keep behavior-preserving where possible; where an addon is dropped (vizcomponent, pdfviewer), leave a visible placeholder with a `// TODO(phase-4b-2)` comment.
- DO NOT use `Notification.show` in code paths that run in unit tests without a UI session.
- Unit tests in `Validation-Manager-Web/src/test`: converter tests use the v8 `Converter` interface — Flow converters are `com.vaadin.flow.data.converter.Converter<Presentation, Model>` with `Result<Model>`; update tests to the Flow API.
- After each cluster: `JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home mvn -q -B -pl Validation-Manager-Web compile` must pass.
- Check the full module before committing: `mvn -q -B -pl Validation-Manager-Web -am compile`.
