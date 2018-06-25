import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { DefaultRoute, Link, Route, RouteHandler } from 'react-router';
import auth from "./auth";
import * as styles from './../../css/structure.css';
import RegistrationModal from './registrationModal';

export default class register extends React.Component {
      constructor(props) {
              super(props);
              this.state = {
                                   error: false
                                 };
      }

      componentDidUpdate() {
        this.refs.registrationModal.openModal();
      }

      componentDidMount() {
        this.refs.registrationModal.openModal();
      }

      render(){
        return (
          <div>
            <RegistrationModal ref="registrationModal" type={this.props.route.type}/>
          </div>
        );
      }
}

register.contextTypes = {
      router: React.PropTypes.object
};