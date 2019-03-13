import React, { Component } from 'react';
import './App.css';

import Button from '@material-ui/core/Button';
import MonacoEditor from 'react-monaco-editor';

// eslint-disable-next-line import/no-webpack-loader-syntax
import * as engine from 'exports-loader?main!../../target/generated/js/teavm/classes';

class App extends Component {

  constructor(props) {
    super(props);
    window.addEventListener('load', () => this.onPageLoad());

    this.state = {
      code: "",
      output: "",
      loaded: false
    }
  }

  onPageLoad() {
    console.log("Page loaded!");
    const time = performance.now();
    engine();
    console.log("Engine init took " + (performance.now() - time) + " milliseconds.")
    this.setState({
      loaded: true
    });
  }

  onCodeEdit(value, event) {
    this.setState({
      code: value
    });
  }

  runCode() {
    if (this.state.loaded) {
      const output = window.execute(this.state.code, "");
      this.setState({
        output: output
      });
    }
  }

  render() {
    let code = this.state.code;
    let result = "Initializing Engine...";
    let buffer = this.state.output;
    if (this.state.loaded) {
      const time = performance.now();
      result = window.compile(code);
      console.log("Compiled code in " + (performance.now() - time) + " milliseconds.")
    }

    const options = {
      selectOnLineNumbers: true
    };
    const editor = (
      <MonacoEditor
        width="800"
        height="600"
        language="javascript"
        theme="vs-dark"
        value={code}
        options={options}
        onChange={(value, event) => this.onCodeEdit(value, event)}
      />
    );
    const compiled = (
      <MonacoEditor
        width="800"
        height="600"
        language="javascript"
        theme="vs-dark"
        value={result}
        options={options}
        onChange={(value, event) => this.onCodeEdit(value, event)}
      />
    );
    const output = (
      <MonacoEditor
        width="800"
        height="600"
        language="javascript"
        theme="vs-dark"
        value={buffer}
        options={options}
      />
    );
    return (
      <div>
        {editor}
        {compiled}
        <Button variant="contained" color="primary" onClick={() => this.runCode()}>
          Run Code
        </Button>
        {output}
      </div>
    );
  }
}

export default App;
