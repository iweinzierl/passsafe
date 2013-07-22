from apiclient.discovery import build
from apiclient.http import MediaFileUpload
from apiclient import errors

from oauth2client.client import OAuth2WebServerFlow
from oauth2client.file import Storage

REDIRECT_URI = 'urn:ietf:wg:oauth:2.0:oob'
OAUTH_SCOPE = 'https://www.googleapis.com/auth/drive'

MIME_TYPE = "text/plain"


class GDriveClient(object):
    def __init__(self, client_id, client_secret, storage_file):
        self.storage = Storage(storage_file)
        self.credentials = self.storage.get()
        if self.credentials is None:
            self.credentials = login(client_id, client_secret, OAUTH_SCOPE, REDIRECT_URI)
            if self.credentials is not None:
                self.storage.put(self.credentials)
        http = httplib2.Http()
        http = credentials.authorize(http)
        self.drive_service = build("drive", "v2", http=http)

    def sync(self, filename):
        """
            This method may be used to synchronize a file to Google Drive storage.
            If filename does not exist, it will be created by uploading the file.
            If filename is already stored in Google Drive, the online file will
            be overriden with the new file.
        """
        file_id = self._search(filename)
        if file_id is None:
            self._upload(filename)
        else:
            self._update(filename, file_id)

    def _search(self, filename):
        page_token = None
        while True:
            try:
                param = {}
                if page_token:
                    param['pageToken'] = page_token
                files = self.drive_service.files().list(**param).execute()

                for item in files['items']:
                    if item['title'] == filename:
                        return item['id']
                page_token = files.get('nextPageToken')
                if not page_token:
                    break
            except errors.HttpError, error:
                print "An error occured: %s" % error
                break

    def _upload(self, filename):
        media_body = MediaFileUpload(filename, mimetype=MIME_TYPE, resumable=True)
        body = {
            "title": "PassSafe Storage",
            "description": "The storage of PassSafe application",
            "mimeType": MIME_TYPE
        }

        file = self.drive_service.files().insert(body, media_body=media_body).execute()

    def _update(self, filename, file_id):
        media_body = MediaFileUpload(filename, mimetype=MIME_TYPE, resumable=True)
        file = self.drive_service.files().update(fileId=file_id, media_body=media_body).execute()


def login(client_id, client_secret, oauth_scope, redirect_url):
    flow = OAuth2WebServerFlow(client_id, client_secret, oauth_scope, redirect_url)
    authorize_url = flow.step1_get_authorize_url()
    print "Go to following link in your browser: " + authorize_url
    code = raw_input("Enter verification code: ").strip()
    return flow.step2_exchange(code)

