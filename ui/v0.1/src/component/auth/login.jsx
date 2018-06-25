import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { DefaultRoute, Link, Route, RouteHandler } from 'react-router';
import * as styles from './../../css/structure.css';
import LoginModal from './loginModal';

export default class login extends React.Component {
      constructor(props) {
        super(props);
      }

      componentDidUpdate() {
       this.refs.loadingModal.openModal();
      }

      componentDidMount() {
       this.refs.loadingModal.openModal();
      }


      render(){
        return (
          <div>
            <LoginModal ref="loadingModal" type={this.props.route.type}/>
          </div>
        );
      }
}