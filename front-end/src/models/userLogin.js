/**
 * class for the users of Solar Sedum
 *
 * @author Noa de Greef
 */

export class userLogin {

    /**
     * Constructor of a user
     *
     * @constructor
     * @param {number} id
     * @param {number} teamId
     * @param {String} username
     * @param {String} email
     * @param {String} password
     * @param type
     */
    constructor(id, teamId, username, email, password, type) {
        this.id = id;
        this.teamId = teamId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type

    }

    static dummyData() {
        return new userLogin(
            1,
            1,
            "Julian",
            "kaasbaas43@gmail.com",
            "Konijn",
            "admin"
        );
    }
}