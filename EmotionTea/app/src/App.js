import React, { Component } from 'react';
import './App.css';

import Button from '@material-ui/core/Button';
import MonacoEditor from 'react-monaco-editor';

class App extends Component {
  hack = null
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
    // eslint-disable-next-line import/no-webpack-loader-syntax
    import('exports-loader?main!../../target/generated/js/teavm/classes').then(engine => {
      console.log(engine);
      console.log("Page loaded!");
      const time = performance.now();
      engine.default();
      console.log("Engine init took " + (performance.now() - time) + " milliseconds.")
      this.setState({
        loaded: true
      });
      if (this.hack != null) {
        const keywords = window.getOpcodes();
        const tokens = [];
        const suggestions = [];
  
        keywords.push("load");
        for (let keyword of keywords) {
          if (keyword.startsWith("UNUSED") || keyword.startsWith("load ") || keyword.startsWith("sdr 0x") || keyword.startsWith("ldr 0x")) {
            continue;
          }
          tokens.push(["^" + keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), "keyword"]);
          suggestions.push({
            label: keyword,
            insertText: keyword,
            kind: keyword === "load" ? this.hack.languages.CompletionItemKind.Constant : this.hack.languages.CompletionItemKind.Function
          })
        }
        this.hack.languages.setMonarchTokensProvider("elang", {
          tokenizer: {
            root: tokens
          }
        });
        this.hack.languages.registerCompletionItemProvider("elang", {
          provideCompletionItems: () => {
            return {
              suggestions: suggestions
            };
          }
        });
      }
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

  mutateEditor(monaco) {
    console.log(monaco)
    this.hack = monaco;
    monaco.editor.defineTheme('emotion', {
      base: 'vs',
      inherit: true,
      rules: [
        { token: 'keyword', foreground: '0000ff' },
      ]
    });
    monaco.languages.register({
      id: "elang"
    });
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
        language="elang"
        theme="emotion"
        value={code}
        options={options}
        onChange={(value, event) => this.onCodeEdit(value, event)}
        editorWillMount={(editor) => this.mutateEditor(editor)}
      />
    );
    const compiled = (
      <MonacoEditor
        width="800"
        height="600"
        language="elang"
        theme="emotion"
        value={result}
        options={options}
        onChange={(value, event) => this.onCodeEdit(value, event)}
      />
    );
    const output = (
      <MonacoEditor
        width="800"
        height="600"
        language="elang"
        theme="emotion"
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
