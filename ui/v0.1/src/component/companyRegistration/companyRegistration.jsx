import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class CompanyRegistration extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.companyRegistration = this.companyRegistration.bind(this);

    }

    companyRegistration(e){
        e.preventDefault();

        this.refs.loading.openModal();

        var data = {
            companyid: auth.getCompanyid(),
            userid:auth.getUserid(),
            token: auth.getToken(),
            companycode: this.refs.companycode.value,
            companyname: this.refs.companyname.value,
            code:10
        };

        let parent = this;

        $.ajax({
            type:"post",
            url:"../commonModules/php/modules/Auth.php/auth/registerCompany",
            data:JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            success: function(json_data) {
               alert(JSON.stringify(json_data.body));
               if(json_data.body.registerationSuccessCode != -1){
                   parent.refs.message.innerText = "会社コードを登録しました。"

                   //setTimeout(function(){
                   // parent.refs.message.innerText = ""
                   //}, 1000);
               }else{
                   parent.refs.message.innerText = "指定された会社コードは既に存在しています。他のコードを入力してください"
               }

               parent.refs.loading.closeModal();
            },
            error: function (request, status, error) {
                alert(request.responseText);
            },
            complete: function() {
            }
        });

    }

    render() {
        return (
            <div>
                <div>
                    <div className={styles.accountRoleChangeTitle}> 会社登録 </div>
                    <div className={styles.accountRoleChangeItemBox}>
                    <div className={styles.accountRoleChangeItemName}>
                        以下の会社情報を入力してください。
                    </div>
                    <input type="text" ref="companycode" placeholder="会社コード"/>
                    <input type="text" ref="companyname" placeholder="会社名"/>
                    </div>

                    <div className={styles.accountRoleChangeButton}>
                        <div className={styles.accountRoleChangeButtonBox}><div onClick={this.companyRegistration.bind(this)} width="200"><img src="../icon/mono_icons/exchange32.png" className={styles.icon}/>変更 <span ref="message"></span></div></div>
                    </div>
                </div>
                 <Loading ref="loading"/>
            </div>
           )
    }
}