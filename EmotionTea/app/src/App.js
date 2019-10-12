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
import DialogTitle from '@material-ui/core/DialogTitle';
import Dialog from '@material-ui/core/Dialog';
import TextField from '@material-ui/core/TextField';

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
      editorGeneratedOpen: false,
      interpreterCode: "",
      interpreterArguments: "",
      interpreterOutput: "",
      decompilerProgram: "",
      toolsRemoveNewlines: "",
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
          tokens.push(["^(\\s*)" + keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + suffix, "keyword"]);
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
    const state = this.encodeState(this.state);
    const url = new URL(window.location);
    url.searchParams.set("state", state);
    window.history.replaceState("statedata", "", url);
  }

  encodeState(state) {
    let obj = undefined;
    switch (state.mode) {
      case "compiler":
        obj = {
          editorCode: state.editorCode,
          editorArguments: state.editorArguments
        };
        break;
      case "interpreter":
        obj = {
          interpreterCode: state.interpreterCode,
          interpreterArguments: state.interpreterArguments
        };
        break;
      default:
        obj = {};
        break;
    }
    obj.mode = state.mode;
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
      this.setState({
        editorOutput: output
      });
    }
  }

  runCodeInterpreter() {
    if (this.state.loaded) {
      const output = this.emotion.interpret(this.state.interpreterCode, this.state.interpreterArguments);
      this.setState({
        interpreterOutput: output
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

  generateStackExchange() {
    if (!this.state.loaded) {
      return "";
    }
    const result = [];
    const compiled = this.emotion.compile(this.state.editorCode);
    if (compiled.program !== undefined) {
      let baseUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
      if (!baseUrl.endsWith("/")) {
        baseUrl += "/";
      }
      const codepageUrl = baseUrl + "?state=" + this.encodeState({ mode: "codepage" });
      const tryItUrl = baseUrl + "?state=" + this.encodeState({ mode: "interpreter", interpreterCode: compiled.program, interpreterArguments: this.state.editorArguments });
      result.push("# [Emotion][1], " + this.getCodeBytes(compiled.program) + " [bytes][2]");
      result.push("");
      result.push("    " + compiled.program);
      result.push("Explanation");
      result.push("");
      for (const line of compiled.explanation) {
        result.push("    " + line);
      }
      result.push("");
      result.push("[Try it online!][3]");
      result.push("");
      result.push("");
      result.push("  [1]: https://github.com/Quantum64/Emotion");
      result.push("  [2]: " + codepageUrl);
      result.push("  [3]: " + tryItUrl);
    } else {
      result.push("Program did not compile!");
    }
    return result.join("\n");
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
                    <Grid container direction="row" alignItems="center" spacing={2} style={{ padding: 5 }}>
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
                      <Grid item>
                        <Button size="small" variant="contained" color="secondary" onClick={() => {
                          this.setState({
                            editorGeneratedOpen: true
                          });
                        }}>
                          Generate StackExchange
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
                    <Grid container direction="row" alignItems="center" spacing={2} style={{ padding: 5 }}>
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
        <GeneratedAnswer
          selectedValue={this.state.selectedValue}
          open={this.state.editorGeneratedOpen}
          onClose={() => {
            this.setState({
              editorGeneratedOpen: false
            });
          }}
          value={() => this.generateStackExchange()}
        />
      </React.Fragment>
    );
  }

  getInterpreterContent() {
    const code = this.state.interpreterCode;
    const args = this.state.interpreterArguments;
    //let result = "Initializing Engine...";
    //let bytes = 0;
    const bytes = this.getCodeBytes(code);

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
                  interpreterCode: value
                });
              }}
              editorWillMount={(editor) => this.mutateEditor(editor)}
            />
          </Grid>
          <br />
          <Grid item>
            <Typography variant="h6">
              Arguments
            </Typography>
          </Grid>
          <Grid item xs>
            <MonacoEditor width="100%" height="100%" language="text" theme="emotion"
              value={args}
              options={optionsArguments}
              onChange={(value, event) => {
                this.setState({
                  interpreterArguments: value
                });
              }}
            />
          </Grid>
          <br />
          <Grid item>
            <Grid container direction="row" alignItems="center" spacing={2} style={{ padding: 5 }}>
              <Grid item>
                <Typography variant="h6">
                  Output
                </Typography>
              </Grid>
              <Grid item>
                <Button size="small" variant="contained" color="primary" onClick={() => this.runCodeInterpreter()}>
                  Run Code
                </Button>
              </Grid>
            </Grid>
          </Grid>
          <Grid item xs>
            <MonacoEditor width="100%" height="100%" language="text" theme="emotion"
              value={this.state.interpreterOutput}
              options={optionsDisabled}
            />
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
        I may write a programming guide at some point when I have time... but for now just try sticking the below opcodes in the compiler and see what happens.
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
        <Typography variant="h6" gutterBottom>
          The codepage is comprised of 256 emoji, each representing one byte.
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

  getToolsContent() {
    return (
      <div>
        <Typography variant="h5">
          Remove Newlines
        </Typography>
        <TextField
          id="outlined-textarea"
          label="Paste Here"
          value={this.state.toolsRemoveNewlines}
          multiline
          fullWidth
          margin="normal"
          variant="outlined"
          onChange={(event) => {
            this.setState({
              toolsRemoveNewlines: event.target.value.replace(/\n/g, "\\n")
            });
          }}
        />
      </div>
    )
  }

  render() {
    this.injectStateUrl();

    let content = null;
    switch (this.state.mode) {
      case "interpreter":
        content = this.getInterpreterContent();
        break;
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
      case "tools":
        content = this.getToolsContent();
        break;
      default:
        break;
    }

    const modes = ["interpreter", "compiler", "lexer", "tools", "reference", "codepage"];
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
            <Grid container spacing={2} alignItems="center">
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

class GeneratedAnswer extends React.Component {
  constructor(props) {
    super(props);
  }

  handleClose = () => {
    this.props.onClose(this.props.selectedValue);
  };

  render() {
    const { classes, onClose, selectedValue, ...other } = this.props;

    return (
      <Dialog fullWidth onClose={this.handleClose} aria-labelledby="simple-dialog-title" {...other}>
        <DialogTitle id="simple-dialog-title">Generated StackExchange Answer</DialogTitle>
        <div style={{ padding: 10 }}>
          <TextField
            id="outlined-textarea"
            label="Copy Me!"
            multiline
            fullWidth
            margin="normal"
            variant="outlined"
            value={this.props.value()}
          />
        </div>
      </Dialog>
    );
  }
}
