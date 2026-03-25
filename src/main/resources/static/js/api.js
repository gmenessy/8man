var Api = (function() {
    var BASE = '/api';

    function request(method, path, body) {
        var opts = {
            method: method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) {
            opts.body = JSON.stringify(body);
        }
        return fetch(BASE + path, opts).then(function(res) {
            return res.json().then(function(data) {
                if (!res.ok) {
                    throw new Error(data.error || 'Request failed');
                }
                return data;
            });
        });
    }

    return {
        createCase: function(caseData) {
            return request('POST', '/cases', caseData);
        },

        getCase: function(id) {
            return request('GET', '/cases/' + id);
        },

        startConsultation: function(caseId) {
            return request('POST', '/cases/' + caseId + '/consult');
        },

        getStatus: function(caseId) {
            return request('GET', '/cases/' + caseId + '/status');
        }
    };
})();
