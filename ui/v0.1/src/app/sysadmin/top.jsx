import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import Modal from 'react-modal';

export default class Top extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    openModal() {
        this.setState({modalIsOpen: true});
    }

    afterOpenModal() {
    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }

    render() {
        return <div>
                    <div> New Arrivals !</div>
               </div>
    }
}