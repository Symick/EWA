export class SessionSbService{
    browserItemStorage;
    resourcesURL;
    currentToken;
    currentUser;


    constructor(resourcesURL, browserItemStorage) {
        this.browserItemStorage = browserItemStorage;
        this.resourcesURL = resourcesURL;
        this.currentUser = null;
        this.currentToken = null;
    }

    getTokenFromBrowserStorage(){
        if (this.currentToken != null){
            return this.currentToken
        }

        this.currentToken = window.localStorage.getItem(this.browserItemStorage);
        let jsonUser = window.localStorage.getItem(this.browserItemStorage+"_ACC");

        if (this.currentToken == null){
            //TODO get the token
            console.log("Implement method TODO")
        }
        if (jsonUser != null){
            this.currentToken = JSON.parse(jsonUser);
        }
        return this.currentToken;

    }

    saveTokenInBrowserStorage(token, account){
        this.currentToken = token;
        this.currentUser = account;

        if (token == null){
            this.currentToken = null;
            window.localStorage.removeItem(this.browserItemStorage);
            window.localStorage.removeItem(this.browserItemStorage+"_ACC");
        } else {
            window.localStorage.setItem(this.browserItemStorage, token);
            window.localStorage.setItem(this.browserItemStorage+"_ACC", JSON.stringify(account));
        }
    }

    async asyncLogin(username, password){
        const body = JSON.stringify({username: username, password: password});
        let response = await fetch(this.resourcesURL +"/login",
            {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: body,
                credentials: 'include'
            })
        if (response.ok){
            let account = await response.json();
            this.saveTokenInBrowserStorage(
                response.headers.get("Authorization"), account);
            return account;
        } else {
            console.log(response)
            return null;
        }
    }

    logOut2(){
        this.saveTokenInBrowserStorage(null, null);
    }

}
