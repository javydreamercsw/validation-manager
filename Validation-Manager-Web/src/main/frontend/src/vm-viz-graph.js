/**
 * Connector for the VizGraph Flow component: renders a DOT string as inline
 * SVG via @viz-js/viz (WebAssembly Graphviz).
 */
import { instance } from "@viz-js/viz";

let vizInstance = null;

export class VmVizGraph extends HTMLElement {
  async connectedCallback() {
    if (!vizInstance) {
      vizInstance = await instance();
    }
    if (this._pending !== undefined) {
      this.renderGraph(this._pending);
    }
  }

  async renderGraph(dot) {
    if (!vizInstance) {
      //WASM still loading; remember and render when connectedCallback lands.
      this._pending = dot;
      return;
    }
    this._pending = dot;
    this.textContent = "";
    const svg = await vizInstance.renderSVGElement(dot);
    this.appendChild(svg);
    if (this.$server) {
      this.$server.handleRendered(this.innerHTML);
    }
  }
}

customElements.define("vm-viz-graph", VmVizGraph);
