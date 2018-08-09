import * as React from 'react';
import * as ReactDOM from 'react-dom';
import auth from "./auth";

export default class logout extends React.Component {
     componentDidUpdate() {
         auth.logout();
         this.context.router.push('/login');
     }

     componentDidMount() {
         auth.logout();
         this.context.router.push('/login');
     }


     render() {
        return <p>Log out</p>
     }
}

logout.contextTypes = {
            router: React.PropTypes.object
};