export default class HttpError extends Error {
    status: number;

    constructor(status: number, message?: string) {
        super(message);
        this.status = status; //Not used for now, but could be usefull, idk
        Object.setPrototypeOf(this, HttpError.prototype); // important for instanceof to work
    }
}