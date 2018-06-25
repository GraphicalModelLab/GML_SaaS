import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import Modal from 'react-modal';

export default class TopNoLogin extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
      this.context.router.push('/'+localStorage.companyId+"/login")
    }

    render() {
        return <div>
                    <div> Login first / Register !</div>
               </div>
    }
}

TopNoLogin.contextTypes = {
            router: React.PropTypes.object
};