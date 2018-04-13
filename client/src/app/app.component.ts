import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../environments/environment';


declare var gapi: any;

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    title = 'Friendly Panda App';
    private highlightedEmotion = "happy";
    public emotionDescription: string;
    public emojiRating: number = 0;
    public selectedEmotion = "none";

    constructor(private http: HttpClient) {

    }

    // from home component, for going to home page and reloading on double click
    restart(){
        this.resetPage();
        window.location.reload();
    }

    resetPage(){
        this.resetSelections();
        this.selectedEmotion = "none";
        this.emojiRating = 0;
        this.emotionDescription = "";
    }

    private resetSelections(){
        let newClass = document.getElementById(this.highlightedEmotion);
        newClass.classList.remove('on');
    }

    signIn() {
        let googleAuth = gapi.auth2.getAuthInstance();

        googleAuth.grantOfflineAccess().then((resp) => {

            localStorage.setItem('isSignedIn', 'true');
            this.sendAuthCode(resp.code);

        });


    }

    signOut() {
        let googleAuth = gapi.auth2.getAuthInstance();


        googleAuth.then(() => {
            googleAuth.signOut();
            localStorage.setItem('isSignedIn', 'false');
            localStorage.setItem("userID", "");


            window.location.reload();
        })
    }

    isSignedIn(): boolean {
        status = localStorage.getItem('isSignedIn');

        if (status == 'true') {
            return true;
        } else {
            return false;
        }
    }

    sendAuthCode(code: string): void {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json'
            }),
        };

        this.http.post(environment.API_URL + "login", {code: code}, httpOptions)
            .subscribe(onSuccess => {
                console.log("Code sent to server");
                console.log(onSuccess["$oid"]);
                localStorage.setItem("userID", onSuccess["$oid"]);
                // window.location.reload();
            }, onFail => {
                console.log("ERROR: Code couldn't be sent to the server");
            });

    }

    handleClientLoad() {
        gapi.load('client:auth2', this.initClient);
    }

    initClient() {

        gapi.client.init({
            'clientId': '1080043572259-h3vk6jgc4skl3uav3g0l13qvlcqpebvu.apps.googleusercontent.com',
            'scope': 'profile email'
        });

    }

    ngOnInit() {
        this.handleClientLoad();

    }
}
