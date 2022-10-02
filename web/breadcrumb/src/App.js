import logo from './logo.svg';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import './App.css';

function App() {
  return (
    <div className="App">
      
      <nav>
        <link to="/">Home</link>
        <link to ="/about">About</link>
      </nav>

      <header className="App-header">
        <h1>Bread Crumb</h1>
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>

      <nav>

      </nav>
        
      <main>

      </main>
    </div>
  );
}

export default App;
