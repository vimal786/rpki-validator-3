import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

import {IWhitelistEntry, IWhitelistResponse} from './whitelist.model';

@Injectable()
export class WhitelistService {

  private _whitelistUrl = '/api/roa-prefix-assertions';
  private _deleteWhitelistEntryUrl = '/api/roa-prefix-assertions/{id}';

  constructor(private _http: HttpClient) {}

  getWhitelist(startFrom: string, pageSize: string, search: string, sortBy: string, sortDirection: string): Observable<IWhitelistResponse> {
    const params = new HttpParams()
      .set('startFrom', startFrom)
      .set('pageSize', pageSize)
      .set('search', search)
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this._http.get<IWhitelistResponse>(this._whitelistUrl, {params: params})
  }

  saveWhitelistEntry(entry: IWhitelistEntry): Observable<any> {
    return this._http.post(this._whitelistUrl, { data: entry });
  }

  deleteWhitelistEntry(entry: IWhitelistEntry): Observable<any> {
    return this._http.delete<IWhitelistResponse>(this._deleteWhitelistEntryUrl.replace('{id}', entry.id))
  }
}
