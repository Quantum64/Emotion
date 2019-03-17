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
const forcePageReladSinceTheEditorSucksAndHasSuperAnnoyingIssuesWithAutomaticResize = true;

class App extends Component {
  editors = [];
  emotion = null;
  hack = null;

  constructor(props) {
    super(props);
    window.addEventListener("load", () => this.onPageLoad());
    window.addEventListener("resize", () => this.updateSize());

    this.state = {
      mode: "compiler",
      editorCode: "",
      editorOutput: "",
      editorArguments: "",
      decompilerProgram: "",
      loaded: false
    }
  }

  componentWillMount() {
    const url = new URL(window.location);
    const param = url.searchParams.get("state");
    if (param !== null) {
      this.decodeState(param);
    }
  }

  onPageLoad() {
    // eslint-disable-next-line import/no-webpack-loader-syntax
    import('exports-loader?main!../../target/generated/js/teavm/classes').then(engine => {
      const time = performance.now();
      engine.default();
      console.log("Engine init took " + (performance.now() - time) + " milliseconds.")
      this.emotion = window.emotion;
      this.setState({
        loaded: true
      });
      if (this.hack != null) {
        const keywords = this.emotion.getOpcodes();
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

  injectStateUrl() {
    const state = this.encodeState();
    const url = new URL(window.location);
    url.searchParams.set("state", state);
    window.history.replaceState("statedata", "", url);
  }

  encodeState() {
    let obj = undefined;
    switch (this.state.mode) {
      case "editor":
        obj = {
          editorCode: this.state.editorCode,
          editorArguments: this.state.editorArguments
        }
        break;
      default:
        obj = {};
        break;
    }
    obj.mode = this.state.mode;
    return btoa(encodeURIComponent(JSON.stringify(obj)));
  }

  decodeState(state) {
    this.setState({
      ...JSON.parse(decodeURIComponent(atob(state)))
    });
  }

  updateSize() {
    for (const editor of this.editors) {
      editor.layout();
    }
  }

  runCodeEditor() {
    if (this.state.loaded) { 
      const output = this.emotion.execute(this.state.editorCode, this.state.editorArguments);
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
    if (forcePageReladSinceTheEditorSucksAndHasSuperAnnoyingIssuesWithAutomaticResize) {
      this.state.mode = mode;
      this.injectStateUrl();
      window.location.reload();
      return;
    }
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
      const compiled = this.emotion.compile(code);
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
              onChange={(value, event) => {
                this.setState({
                  editorCode: value
                });
                this.injectStateUrl();
              }}
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
    let code = this.state.decompilerProgram;
    let result = "Initializing Engine...";
    if (this.state.loaded) {
      result = this.emotion.decompile(code);
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
    if (this.state.loaded) {
      for (let opcode of this.emotion.getOpcodes()) {
        if (!this.isValidOpcode(opcode)) {
          continue;
        }
        rows.push(
          <TableRow key={opcode}>
            <TableCell>
              {opcode}
            </TableCell>
            <TableCell>
              {this.emotion.getOpcodeDescription(opcode)}
            </TableCell>
          </TableRow>);
      }
    } else {
      rows.push(
        <TableRow key="loading">
          <TableCell>
            Loading
          </TableCell>
          <TableCell>
            The engine has not fully loaded yet.
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

  getCodepageContent() {
    const rows = [];
    if (this.state.loaded) {
      let index = 0;
      for (let character of this.emotion.getCodepage()) {
        rows.push(
          <TableRow key={character}>
            <TableCell>
              {index}
            </TableCell>
            <TableCell>
              0x{index.toString(16)}
            </TableCell>
            <TableCell>
              {character}
            </TableCell>
          </TableRow>);
        index++;
      }
    } else {
      rows.push(
        <TableRow key="loading">
          <TableCell>
            Loading Engine
          </TableCell>
        </TableRow>);
    }
    return (
      <div>
        <Typography variant="h2" style={{ color: "grey" }}>
          Codepage
        </Typography>
        <Paper>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Id</TableCell>
                <TableCell>Hex</TableCell>
                <TableCell>Character</TableCell>
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
      case "compiler":
        content = this.getEditorContent();
        break;
      case "reference":
        content = this.getReferenceContent();
        break;
      case "lexer":
        content = this.getDecompileContent();
        break;
      case "codepage":
        content = this.getCodepageContent();
        break;
      default:
        break;
    }

    const modes = ["interpreter", "compiler", "lexer", "debugger", "reference", "codepage"];
    const buttons = [];
    for (let mode of modes) {
      buttons.push(
        <Grid item key={mode}>
          <Button variant="outlined" size="small" color="default" style={{ color: "white" }} onClick={() => this.updateMode(mode)}>
            {mode}
          </Button>
        </Grid>
      );
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
              {buttons}
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
