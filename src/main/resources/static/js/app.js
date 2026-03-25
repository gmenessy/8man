var App = (function() {
    var currentCaseId = null;
    var pollInterval = null;

    function init() {
        document.getElementById('case-form').addEventListener('submit', handleSubmit);
        document.getElementById('new-case-btn').addEventListener('click', resetToForm);
        document.getElementById('retry-btn').addEventListener('click', resetToForm);
    }

    function handleSubmit(e) {
        e.preventDefault();
        var btn = e.target.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = 'Wird erstellt...';

        var caseData = {
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            question: document.getElementById('question').value,
            goal: document.getElementById('goal').value
        };

        Api.createCase(caseData)
            .then(function(created) {
                currentCaseId = created.id;
                return Api.startConsultation(currentCaseId);
            })
            .then(function(status) {
                UI.show('progress-view');
                UI.renderAgents(status.agents);
                startPolling();
            })
            .catch(function(err) {
                UI.showError('Fehler beim Starten der Beratung: ' + err.message);
            })
            .finally(function() {
                btn.disabled = false;
                btn.textContent = 'Expertenrat starten';
            });
    }

    function startPolling() {
        if (pollInterval) clearInterval(pollInterval);
        pollInterval = setInterval(pollStatus, 2000);
    }

    function pollStatus() {
        if (!currentCaseId) return;

        Api.getStatus(currentCaseId)
            .then(function(status) {
                if (status.currentPhase) {
                    UI.updatePhaseTracker(status.currentPhase);
                    var phaseText = {
                        ANALYSIS: 'Agenten analysieren den Fall...',
                        CONSENSUS: 'Konsensbildung läuft...',
                        DISSENT: 'Der Achte Mann formuliert Kritik...',
                        SYNTHESIS: 'Finale Synthese wird erstellt...'
                    };
                    UI.updateProgress(status.progress, phaseText[status.currentPhase] || 'Verarbeitung...');
                }

                if (status.complete) {
                    stopPolling();
                    UI.updateProgress(100, 'Abgeschlossen!');
                    setTimeout(function() {
                        UI.renderResults(status.result);
                        UI.show('results-view');
                    }, 500);
                }

                if (status.failed) {
                    stopPolling();
                    UI.showError('Beratung fehlgeschlagen: ' + (status.error || 'Unbekannter Fehler'));
                }
            })
            .catch(function(err) {
                // Don't stop polling on transient errors
                console.error('Poll error:', err);
            });
    }

    function stopPolling() {
        if (pollInterval) {
            clearInterval(pollInterval);
            pollInterval = null;
        }
    }

    function resetToForm() {
        stopPolling();
        currentCaseId = null;
        document.getElementById('case-form').reset();
        UI.show('case-form-view');
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    return {
        resetToForm: resetToForm
    };
})();
