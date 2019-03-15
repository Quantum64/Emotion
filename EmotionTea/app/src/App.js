import React, { Component } from 'react';
import './App.css';

import Grid from '@material-ui/core/Grid';
import Button from '@material-ui/core/Button';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import CssBaseline from '@material-ui/core/CssBaseline';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

import MonacoEditor from 'react-monaco-editor';

const automaticLayout = false;

class App extends Component {
  editors = []
  hack = null;

  constructor(props) {
    super(props);
    window.addEventListener("load", () => this.onPageLoad());
    window.addEventListener("resize", () => this.updateSize());

    this.state = {
      mode: "editor",
      editorCode: "",
      editorOutput: "",
      editorArguments: "",
      decompilerProgram: "",
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
          if (!this.isValidOpcode(keyword)) {
            continue;
          }
          const suffix = keyword.startsWith("load") ? "" : "$";
          tokens.push(["^" + keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + suffix, "keyword"]);
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

  isValidOpcode(keyword) {
    if (keyword.startsWith("UNUSED") || keyword.startsWith("load ") || keyword.startsWith("sdr 0x") || keyword.startsWith("ldr 0x")) {
      return false;
    }
    return true;
  }

  updateSize() {
    for (const editor of this.editors) {
      editor.layout();
    }
  }

  runCodeEditor() {
    if (this.state.loaded) {
      const output = window.execute(this.state.editorCode, this.state.editorArguments);
      console.log(this.state.editorArguments)
      this.setState({
        editorOutput: output
      });
    }
  }

  mutateEditor(monaco) {
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

  getCodeBytes(code) {
    return [...code].length;
  }

  updateMode(mode) {
    this.setState({
      mode: mode
    });
  }

  getEditorContent() {
    let code = this.state.editorCode;
    let result = "Initializing Engine...";
    let bytes = 0;
    if (this.state.loaded) {
      const time = performance.now();
      const compiled = window.compile(code);
      result = compiled.output;
      if (compiled.program !== undefined) {
        bytes = this.getCodeBytes(compiled.program);
      }
      console.log("Compiled code in " + (performance.now() - time) + " milliseconds.")
    }

    const options = { selectOnLineNumbers: true, automaticLayout: automaticLayout };
    const optionsArguments = { ...options, minimap: { enabled: false } }
    const optionsDisabled = { ...optionsArguments, readOnly: true }
    return (
      <React.Fragment>
        <Grid container direction="column" spacing={0} style={{ height: "100%" }}>
          <Grid item>
            <Typography variant="h6">
              Code ({bytes} bytes)
            </Typography>
          </Grid>
          <Grid item xs>
            <MonacoEditor width="100%" height="100%" language="elang" theme="emotion"
              value={code}
              options={options}
              onChange={(value, event) => this.setState({
                editorCode: value
              })}
              editorWillMount={(editor) => this.mutateEditor(editor)}
            />
          </Grid>
          <br />
          <Grid item>
            <Typography variant="h6">
              Compiler Output
            </Typography>
          </Grid>
          <Grid item xs>
            <MonacoEditor width="100%" height="100%" language="text" theme="emotion"
              value={result}
              options={optionsDisabled}
            />
          </Grid>
          <br />
          <Grid item xs>
            <Grid container style={{ height: "100%" }}>
              <Grid item xs>
                <Grid container direction="column" spacing={0} style={{ height: "100%" }}>
                  <Grid item>
                    <Grid container direction="row" alignItems="center" spacing={16} style={{ padding: 5 }}>
                      <Grid item>
                        <Typography variant="h6">
                          Program Output
                        </Typography>
                      </Grid>
                      <Grid item>
                        <Button size="small" variant="contained" color="primary" onClick={() => this.runCodeEditor()}>
                          Run Code
                        </Button>
                      </Grid>
                    </Grid>
                  </Grid>
                  <Grid item xs>
                    <MonacoEditor width="100%" height="100%" language="text" theme="emotion"
                      value={this.state.editorOutput}
                      options={optionsDisabled}
                    />
                  </Grid>
                </Grid>
              </Grid>
              <Grid item xs>
                <Grid container direction="column" spacing={0} style={{ height: "100%" }}>
                  <Grid item>
                    <Grid container direction="row" alignItems="center" spacing={16} style={{ padding: 5 }}>
                      <Grid item>
                        <Typography variant="h6">
                          Arguments
                        </Typography>
                      </Grid>
                    </Grid>
                  </Grid>
                  <Grid item xs>
                    <MonacoEditor width="100%" height="100%" language="text" theme="emotion"
                      value={this.state.editorArguments}
                      options={optionsArguments}
                      onChange={(value, event) => {
                        this.setState({
                          editorArguments: value
                        });
                      }}
                    />
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </React.Fragment>
    );
  }

  getDecompileContent() {
    console.log("gi")
    let code = this.state.decompilerProgram;
    let result = "Initializing Engine...";
    let bytes = 0;
    if (this.state.loaded) {
      result = window.decompile(code);
    }

    const options = { selectOnLineNumbers: true, automaticLayout: automaticLayout };
    const optionsArguments = { ...options, minimap: { enabled: false } }
    const optionsDisabled = { ...optionsArguments, readOnly: true }
    return (
      <React.Fragment>
        <Grid container direction="column" spacing={0} style={{ height: "100%" }}>
          <Grid item>
            <Typography variant="h6">
              Program
            </Typography>
          </Grid>
          <Grid item xs>
            <MonacoEditor width="100%" height="100%" language="text" theme="emotion"
              value={code}
              options={options}
              onChange={(value, event) => this.setState({
                decompilerProgram: value
              })}
              editorWillMount={(editor) => this.mutateEditor(editor)}
            />
          </Grid>
          <br />
          <Grid item>
            <Typography variant="h6">
              Lexer Output
            </Typography>
          </Grid>
          <Grid item xs>
            <MonacoEditor width="100%" height="100%" language="elang" theme="emotion"
              value={result}
              options={optionsDisabled}
            />
          </Grid>    
        </Grid>
      </React.Fragment>
    );
  }

  getReferenceContent() {
    const rows = [];
    for (let opcode of window.getOpcodes()) {
      if (!this.isValidOpcode(opcode)) {
        continue;
      }
      rows.push(
        <TableRow key={opcode}>
          <TableCell>
            {opcode}
          </TableCell>
          <TableCell>
            {window.getOpcodeName(opcode)}
          </TableCell>
        </TableRow>);
    }
    return (
      <div>
        <Typography variant="h2" style={{ color: "grey" }}>
          Emotion Reference
        </Typography>
        <Paper>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Opcode</TableCell>
                <TableCell>Description</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {rows}
            </TableBody>
          </Table>
        </Paper>
      </div>
    );
  }

  render() {
    let content = null;
    switch (this.state.mode) {
      case "editor":
        content = this.getEditorContent();
        break;
      case "reference":
        content = this.getReferenceContent();
        break;
      case "decompile":
        content = this.getDecompileContent();
        break;
    }
    return (
      <div style={{ height: "100%" }}>
        <CssBaseline />
        <AppBar position="static">
          <Toolbar>
            <Grid container spacing={16} alignItems="center">
              <Grid item>
                <Typography variant="h6" color="inherit" noWrap>
                  Emotion
                </Typography>
              </Grid>
              <Grid item>
                <Button variant="outlined" size="small" color="default" style={{ color: "white" }}>
                  Interpreter
                </Button>
              </Grid>
              <Grid item>
                <Button variant="outlined" size="small" color="default" style={{ color: "white" }} onClick={() => this.updateMode("editor")}>
                  Compiler
                </Button>
              </Grid>
              <Grid item>
                <Button variant="outlined" size="small" color="default" style={{ color: "white" }} onClick={() => this.updateMode("decompile")}>
                  Lexer
                </Button>
              </Grid>
              <Grid item>
                <Button variant="outlined" size="small" color="default" style={{ color: "white" }} onClick={() => this.updateMode("reference")}>
                  Reference
                </Button>
              </Grid>
            </Grid>
          </Toolbar>
        </AppBar>
        <Grid container justify="center" style={{ padding: 20, height: "90%" }}>
          <Grid item xs>
            {content}
          </Grid>
        </Grid>
      </div>
    );
  }
}

export default App;
