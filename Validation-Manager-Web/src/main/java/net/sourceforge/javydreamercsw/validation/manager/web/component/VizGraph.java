/*
 * Copyright 2026 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.javydreamercsw.validation.manager.web.component;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Minimal Vaadin Flow wrapper for {@code @viz-js/viz} (a WebAssembly build of
 * Graphviz, MIT licensed). Renders a DOT graph as an inline SVG inside this
 * element.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Tag("vm-viz-graph")
@NpmPackage(value = "@viz-js/viz", version = "3.11.0")
@JsModule("./src/vm-viz-graph.js")
public class VizGraph extends Component implements HasSize {

    private final List<Consumer<String>> renderListeners = new ArrayList<>();

    /**
     * Set the DOT source and (re)render the graph. Rendering is asynchronous
     * (the Graphviz WASM instance loads on first use); any error is surfaced
     * as text inside the component.
     *
     * @param dot the DOT source to render
     */
    public void setGraph(String dot) {
        getElement().executeJs(
                "this.renderGraph($0).catch(e => this.textContent = String(e))",
                dot);
    }

    /**
     * Run a callback with the rendered graph's SVG markup once rendering
     * finished (empty string if the last render failed).
     *
     * @param callback receives the SVG string
     */
    public void onRendered(Consumer<String> callback) {
        renderListeners.add(callback);
    }

    @ClientCallable
    private void handleRendered(String svg) {
        List<Consumer<String>> snapshot = new ArrayList<>(renderListeners);
        renderListeners.clear();
        snapshot.forEach(callback -> callback.accept(svg == null ? "" : svg));
    }

    /**
     * Remove the rendered graph.
     */
    public void clear() {
        getElement().executeJs("this.textContent = ''");
    }
}
