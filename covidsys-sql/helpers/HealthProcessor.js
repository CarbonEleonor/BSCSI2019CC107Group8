class HealthProcessor{
    healthScore = 0;    
    healthStatus = "";
    answers = [];
        constructor(JSONString = '{}'){
        if(typeof JSONString === 'string')
            this.JSONObject = JSON.parse(JSONString);
            
        this.JSONObject = JSONString;
    }

    // based on score, return a health status
    // score chart
    // 0 - 2 : Sus
    // 3 : Pending, subject for review
    // 4-6 : Healthy

    init(){
        this.#getAnswers();
        this.answers.forEach(answer => {
            this.#analyzeAnswer(answer);
        });
        this.#processStatus();
        return this;
    }
    #processStatus(){
        switch(this.healthScore){
            case 0:
            case 1:
            case 2: 
                this.healthStatus = "risk";
                break;
            case 3:
                this.healthStatus = "pending";
                break;
            case 4:
            case 5:
            case 6:
                this.healthStatus = "healthy";
                break;
            default:
                this.healthStatus = "pending";
                break;
        }
    }
    getStatus(){
        return this.healthStatus;
    }
    getScore(){
        return this.healthScore;
    }
    #getAnswers(){
        this.answers = Object.values(this.JSONObject);
    }
    #analyzeAnswer(answer = ""){
        let score = Number(answer.trim()[0]);

        if(score > 6 || score < 0)
            return 0;
        
        this.healthScore += score;
    }
}
module.exports = HealthProcessor;